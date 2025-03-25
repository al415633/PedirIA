const { createApp } = Vue;
const RETRIEVE_ONE_HORTOFRUTICOLA = "/hortofruticolas/";
const STOCK_RETRIEVE_ONE = "/hortofruticolas/stock/producto/";
const ADD_STOCK = "/hortofruticolas/stock";
const API_STOCK = "/hortofruticolas/stock/";
const API_HISTORICO = "/hortofruticolas/stock/historico/";
const API_HORTOFRUTICOLA = "/hortofruticolas/";
const VALIDAR_HORTOFRUTICOLA = "/hortofruticolas/validar";

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
            editingHortofruticola: { nombre: '', unidad: '', tipoConserva: '' },
            tiposConserva: ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"],
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
            axios.get(RETRIEVE_ONE_HORTOFRUTICOLA + id)
                .then(response => {
                    this.product = response.data;
                })
                .catch(error => {
                    console.error("Error al cargar detalles del producto:", error);
                });
        },
        addStock() {
            const params = new URLSearchParams(window.location.search);
            const idHortofruticola = params.get('id');

            if (!idHortofruticola) {
                this.showToast("Error: Producto no encontrado", "bg-danger");
                return;
            }

            const stockData = {
                cantidad: parseFloat(this.newStock.cantidad),
                fechaIngreso: this.newStock.fechaIngreso,
                fechaVencimiento: this.newStock.fechaVencimiento,
                producto: { id: idHortofruticola }
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
                    this.activeTab = "historico"; // 📌 Cambiar de pestaña después de cargar los datos
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
        showToast(message, bgClass) {
            this.toastMessage = message;
            const toastEl = document.getElementById("toastMessage");
            toastEl.className = `toast custom-toast text-white ${bgClass} show`;
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
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
        openEditHortofruticolaModal() {
            this.editingHortofruticola = { ...this.product }; // Copiar datos actuales
            this.nombreError = '';
            this.unidadError = '';
            this.isInvalid = false;
            new bootstrap.Modal(document.getElementById("editHortofruticolaModal")).show();
        },
        validateEditHortofruticola() {
            const nombre = this.editingHortofruticola.nombre.trim().toLowerCase();
            const unidad = this.editingHortofruticola.unidad.trim().toLowerCase();

            // No validar si no se ha cambiado nada
            if (nombre === this.product.nombre.trim().toLowerCase() && unidad === this.product.unidad.trim().toLowerCase()) {
                this.nombreError = '';
                this.unidadError = '';
                this.isInvalid = false;
                return;
            }

            axios.get(`${VALIDAR_HORTOFRUTICOLA}?nombre=${encodeURIComponent(nombre)}&unidad=${encodeURIComponent(unidad)}`)
                .then(response => {
                    if (response.data.existe) {
                        this.nombreError = "Ya existe un hortofruticola con este nombre y unidad.";
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
        updateHortofruticola() {
            if (this.isInvalid) return; // 📌 No permitir actualizar si hay error

            axios.put(API_HORTOFRUTICOLA, this.editingHortofruticola)
                .then(() => {
                    this.product = { ...this.editingHortofruticola }; // Actualizar UI
                    bootstrap.Modal.getInstance(document.getElementById("editHortofruticolaModal")).hide();
                    this.showToast("✔️ Hortofruticola actualizada correctamente.", "bg-success");
                })
                .catch(error => {
                    console.error("Error al actualizar hortofruticola:", error);
                    this.showToast("❌ Error al actualizar la hortofruticola.", "bg-danger");
                });
        },

        showToast(message, bgClass) {
            this.toastMessage = message;
            const toastEl = document.getElementById("editToast");
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