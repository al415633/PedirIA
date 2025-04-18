const { createApp } = Vue;

const RETRIEVE_ONE_CARNE = "/carnes/";
const STOCK_RETRIEVE_ONE = "/carnes/stock/producto/";
const ADD_STOCK = "/carnes/stock";
const API_STOCK = "/carnes/stock/";
const API_HISTORICO = "/carnes/stock/historico/";
const API_CARNE = "/carnes/";
const VALIDAR_CARNE = "/carnes/validar";
const API_OFERTAS = "/oferta";

createApp({
    data() {
        return {
            product: {},
            currentStock: [],
            newStock: {
                cantidad: '',
                fechaIngreso: '',
                fechaVencimiento: ''
            },
            toastMessage: '',
            editingStock: { id: null, cantidad: '', fechaIngreso: '', fechaVencimiento: '' },
            sellStockData: { id: null, cantidad: '', disponible: 0 },
            offerStockData: { id: null, cantidad: '', fechaIngreso: '', fechaVencimiento: '' },
            historicoStock: [],
            activeTab: "stock",
            editingCarne: { nombre: '', unidad: '', tipoConserva: '' },
            tiposConserva: ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"],
            nombreError: '',
            unidadError: '',
            isInvalid: false,
            ofertas: [],
            nuevaOferta: {
                ubicacion: '',
                cantidad: '',
            },
            editingOferta: { ubicacion: '' },
            selectedOffer: {},

            // Variables de paginación
            currentPageStock: 1,
            currentPageHistorico: 1,
            itemsPerPage: 7, // 7 items por página
        };
    },
    computed: {
        // Stock Actual paginado
        sortedCurrentStockPaginated() {
            const start = (this.currentPageStock - 1) * this.itemsPerPage;
            return this.currentStock
                .slice(start, start + this.itemsPerPage)
                .map(stock => {
                    return {
                        ...stock,
                        cantidadFormateada: this.formatNumber(stock.cantidad) + " " + (this.product.unidad || "") // Agregar formato de cantidad
                    };
                });
        },

        // Histórico de Ventas paginado
        sortedCurrentHistoricoPaginated() {
            const start = (this.currentPageHistorico - 1) * this.itemsPerPage;
            return this.historicoStock
                .slice(start, start + this.itemsPerPage)
                .map(historico => {
                    return {
                        ...historico,
                        cantidadFormateada: this.formatNumber(historico.cantidad) + " " + (this.product.unidad || "") // Formatear la cantidad
                    };
                });
        },

        // Total de páginas para Stock Actual
        totalPagesStock() {
            return Math.ceil(this.currentStock.length / this.itemsPerPage);
        },
        // Total de páginas para Histórico de Ventas
        totalPagesHistorico() {
            return Math.ceil(this.historicoStock.length / this.itemsPerPage);
        }
    },
    methods: {
        loadProductDetails() {
            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');
            axios.get(RETRIEVE_ONE_CARNE + id)
                .then(response => {
                    this.product = response.data;
                })
                .catch(error => {
                    console.error("Error al cargar detalles del producto:", error);
                });
        },
        loadCurrentStock() {
            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');
            axios.get(STOCK_RETRIEVE_ONE + id)
                .then(response => {
                    this.currentStock = response.data;
                    this.activeTab = "stock";
                })
                .catch(error => {
                    console.error("Error al cargar stock actual:", error);
                });
        },
        loadHistorico() {
            const id = new URLSearchParams(window.location.search).get('id');
            axios.get(API_HISTORICO + id)
                .then(response => {
                    this.historicoStock = response.data;
                    this.activeTab = "historico";
                })
                .catch(error => console.error("Error al cargar el historial:", error));
        },
        loadOfertasPublicadas() {
            axios.get(API_OFERTAS + "/mis-ofertas-publicadas/carne/" + this.product.id)
                .then(response => {
                    this.ofertas = response.data;
                    this.ofertas.sort((a, b) => {
                        const fechaA = new Date(a.productoOferta.stock.fechaVencimiento);
                        const fechaB = new Date(b.productoOferta.stock.fechaVencimiento);
                        return fechaA - fechaB;
                    });
                    this.activeTab = "ofertasPublicadas";
                })
                .catch(error => {
                    console.error("Error al cargar ofertas:", error);
                });
        },
        addStock() {
            const params = new URLSearchParams(window.location.search);
            const idCarne = params.get('id');

            if (!idCarne) {
                this.showToast("Error: Producto no encontrado", "bg-danger");
                return;
            }

            if (new Date(this.newStock.fechaIngreso) >= new Date(this.newStock.fechaVencimiento)) {
                this.showToast("Error: La fecha de ingreso debe ser anterior a la fecha de vencimiento", "bg-danger");
                return;
            }

            const stockData = {
                cantidad: parseFloat(this.newStock.cantidad),
                fechaIngreso: this.newStock.fechaIngreso,
                fechaVencimiento: this.newStock.fechaVencimiento,
                producto: { id: idCarne }
            };

            axios.post(ADD_STOCK, stockData)
                .then(response => {
                    this.showToast(`${this.formatNumber(stockData.cantidad)} ${this.product.unidad} añadidas. Ingreso: ${this.formatDate(stockData.fechaIngreso)}, Vence: ${this.formatDate(stockData.fechaVencimiento)}`, "bg-success");
                    this.loadCurrentStock();
                    this.newStock = { cantidad: '', fechaIngreso: '', fechaVencimiento: '' };
                })
                .catch(error => {
                    this.showToast("Error al agregar stock", "bg-danger");
                });
        },
        editStock(stock) {
            this.editingStock = JSON.parse(JSON.stringify(stock));
            new bootstrap.Modal(document.getElementById("editStockModal")).show();
        },
        updateStock() {
            axios.put(API_STOCK + this.editingStock.id, this.editingStock)
                .then(() => {
                    bootstrap.Modal.getInstance(document.getElementById("editStockModal")).hide();
                    this.loadProductDetails();
                    this.loadCurrentStock();
                })
                .catch(error => console.error("Error al actualizar stock:", error));
        },
        showToast(message, bgClass) {
            this.toastMessage = message;
            const toastEl = document.getElementById("toastMessage");
            toastEl.className = `toast custom-toast text-white ${bgClass} show`;
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        },
        openSellStock(stock) {
            this.sellStockData = { id: stock.id, cantidad: '', disponible: stock.cantidad };
            new bootstrap.Modal(document.getElementById("sellStockModal")).show();
        },
        sellStock() {
            const cantidadAVender = parseFloat(this.sellStockData.cantidad);
            const disponible = parseFloat(this.sellStockData.disponible);
            if (cantidadAVender > disponible) {
                this.showToast("Error: La cantidad a vender supera la disponible", "bg-danger");
                return;
            }

            axios.post(API_STOCK + "vender/" + this.sellStockData.id + "/" + cantidadAVender)
                .then(() => {
                    this.showToast(`Venta realizada: ${this.formatNumber(cantidadAVender)} ${this.product.unidad}`, "bg-success");
                    this.loadCurrentStock();
                    bootstrap.Modal.getInstance(document.getElementById("sellStockModal")).hide();
                })
                .catch(error => {
                    console.error("Error al vender stock:", error);
                    this.showToast("Error al vender stock", "bg-danger");
                });
        },
        formatDate(dateStr) {
            return new Date(dateStr).toLocaleDateString('es-ES', { year: 'numeric', month: '2-digit', day: '2-digit' });
        },
        formatNumber(value) {
            const num = parseFloat(value);
            if (Number.isInteger(num)) {
                return num.toLocaleString('es-ES', { minimumFractionDigits: 0 });
            }
            return num.toLocaleString('es-ES', { minimumFractionDigits: 0, maximumFractionDigits: 3 });
        },
        // Métodos de Paginación para Stock
        prevPageStock() {
            if (this.currentPageStock > 1) {
                this.currentPageStock--;
            }
        },
        nextPageStock() {
            if (this.currentPageStock < this.totalPagesStock) {
                this.currentPageStock++;
            }
        },
        goToPageStock(page) {
            this.currentPageStock = page;
        },
        // Métodos de Paginación para Histórico
        prevPageHistorico() {
            if (this.currentPageHistorico > 1) {
                this.currentPageHistorico--;
            }
        },
        nextPageHistorico() {
            if (this.currentPageHistorico < this.totalPagesHistorico) {
                this.currentPageHistorico++;
            }
        },
        goToPageHistorico(page) {
            this.currentPageHistorico = page;
        }
    },
    mounted() {
        this.loadProductDetails();
        this.loadCurrentStock();
        this.loadHistorico();
    }
}).mount("#app");
