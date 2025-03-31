const { createApp } = Vue;
const RETRIEVE_ONE = "/pescados/";
const STOCK_RETRIEVE_ONE = "/pescados/stock/producto/";
const ADD_STOCK = "/pescados/stock";
const API_STOCK = "/pescados/stock/";
const API_HISTORICO = "/pescados/stock/historico/";
const API_PESCADO = "/pescados/";
const VALIDAR = "/pescados/validar";

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
            sellStockData: { id: null, cantidad: '' },
            historicoStock: [],
            activeTab: "stock",
            editingProduct: { nombre: '', unidad: '', tipoConserva: '' },
            tiposConserva: ["REFRIGERADO", "FRESCO", "CONGELADO", "VIVO"],
            nombreError: '',
            unidadError: '',
            isInvalid: false
        };
    },
    computed: {
        sortedCurrentStock() {
            return [...this.currentStock]
                .sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento))
                .map(stock => ({
                    ...stock,
                    cantidad: this.formatNumber(stock.cantidad) // 📌 Formatea la cantidad
                }));
        }
    },
    methods: {
        loadProductDetails() {
            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');
            axios.get(RETRIEVE_ONE + id)
                .then(response => {
                    this.product = response.data;
                })
                .catch(error => {
                    console.error("Error al cargar detalles del producto:", error);
                });
        },
        addStock() {
            const params = new URLSearchParams(window.location.search);
            const idPescado = params.get('id');

            if (!idPescado) {
                this.showToast("Error: Producto no encontrado", "bg-danger");
                return;
            }

            const stockData = {
                cantidad: parseFloat(this.newStock.cantidad),
                fechaIngreso: this.newStock.fechaIngreso,
                fechaVencimiento: this.newStock.fechaVencimiento,
                producto: { id: idPescado }
            };

            axios.post(ADD_STOCK, stockData)
                .then(response => {
                    this.showToast(`${stockData.cantidad} unidades añadidas. Ingreso: ${this.formatDate(stockData.fechaIngreso)}, Vence: ${this.formatDate(stockData.fechaVencimiento)}`, "bg-primary");
                    this.loadCurrentStock();
                    this.newStock = { cantidad: '', fechaIngreso: '', fechaVencimiento: '' };
                })
                .catch(error => {
                    this.showToast("Error al agregar stock", "bg-danger");
                });
        },
        loadCurrentStock() {
            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');
            axios.get(STOCK_RETRIEVE_ONE + id)
                .then(response => {
                    this.currentStock = response.data;
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
        editStock(stock) {
            this.editingStock = JSON.parse(JSON.stringify(stock)); // Evita modificar el original directamente
            new bootstrap.Modal(document.getElementById("editStockModal")).show();
        },
        updateStock() {
            axios.put(API_STOCK + this.editingStock.id, this.editingStock)
                .then(() => {
                    this.loadCurrentStock();
                    bootstrap.Modal.getInstance(document.getElementById("editStockModal")).hide();
                })
                .catch(error => console.error("Error al actualizar stock:", error));
        },
        openSellStock(stock) {
            this.sellStockData = { id: stock.id, cantidad: '' };
            new bootstrap.Modal(document.getElementById("sellStockModal")).show();
        },
        sellStock() {
            axios.post(API_STOCK + "vender/" + this.sellStockData.id + "/" +this.sellStockData.cantidad)
                .then(() => {
                    this.loadCurrentStock(); // Recargar stock actualizado
                    bootstrap.Modal.getInstance(document.getElementById("sellStockModal")).hide();
                })
                .catch(error => console.error("Error al vender stock:", error));
        },
        formatDate(dateStr) {
            return new Date(dateStr).toLocaleDateString('es-ES', { year: 'numeric', month: '2-digit', day: '2-digit' });
        },
        formatNumber(value) {
            return new Intl.NumberFormat('es-ES', { minimumFractionDigits: 3, maximumFractionDigits: 3 }).format(value);
        },
        openEditProductModal() {
            this.editingProduct = { ...this.product }; // Copiar datos actuales
            this.nombreError = '';
            this.unidadError = '';
            this.isInvalid = false;
            new bootstrap.Modal(document.getElementById("editProductModal")).show();
        },
        validateEditProduct() {
            const nombre = this.editingProduct.nombre.trim().toLowerCase();
            const unidad = this.editingProduct.unidad.trim().toLowerCase();

            // No validar si no se ha cambiado nada
            if (nombre === this.product.nombre.trim().toLowerCase() && unidad === this.product.unidad.trim().toLowerCase()) {
                this.nombreError = '';
                this.unidadError = '';
                this.isInvalid = false;
                return;
            }

            axios.get(`${VALIDAR}?nombre=${encodeURIComponent(nombre)}&unidad=${encodeURIComponent(unidad)}`)
                .then(response => {
                    if (response.data.existe) {
                        this.nombreError = "Ya existe un pescado con este nombre y unidad.";
                        this.unidadError = "Por favor, elija un nombre o unidad diferente.";
                        this.isInvalid = true;
                    } else {
                        this.nombreError = '';
                        this.unidadError = '';
                        this.isInvalid = false;
                    }
                })
                .catch(error => {
                    console.error("Error en la validación:", error);
                    this.nombreError = "Error al validar el nombre.";
                    this.isInvalid = true;
                });
        },
        updateProduct() {
            if (this.isInvalid) return;

            axios.put(API_PESCADO, this.editingProduct)
                .then(() => {
                    this.product = { ...this.editingProduct };
                    bootstrap.Modal.getInstance(document.getElementById("editProductModal")).hide();
                    this.showToast("✔️ Pescado actualizado correctamente.", "bg-success");
                })
                .catch(error => {
                    console.error("Error al actualizar el pescado:", error);
                    this.showToast("❌ Error al actualizar el pescado.", "bg-danger");
                });
        },
        showToast(message, bgClass) {
            this.toastMessage = message;
            const toastEl = document.getElementById("toastMessage");
            toastEl.className = `toast custom-toast text-white ${bgClass} show`;
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        }
    },
    mounted() {
        this.loadProductDetails();
        this.loadCurrentStock();
    }
}).mount("#app");