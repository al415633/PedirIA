const { createApp } = Vue;
const RETRIEVE_ONE_CARNE = "/carnes/";
const STOCK_RETRIEVE_ONE = "/carnes/stock/producto/";
const ADD_STOCK = "/carnes/stock";
const API_STOCK = "/carnes/stock/";
const API_HISTORICO = "/carnes/stock/historico/";
const API_CARNE = "/carnes/";
const VALIDAR_CARNE = "/carnes/validar";

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
            historicoStock: [],
            activeTab: "stock",
            editingCarne: { nombre: '', unidad: '', tipoConserva: '' },
            tiposConserva: ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"],
            nombreError: '',
            unidadError: '',
            isInvalid: false
        };
    },
    computed: {
        // Se crea un array con la cantidad formateada y la unidad concatenada.
        sortedCurrentStock() {
            return [...this.currentStock]
                .sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento))
                .map(stock => {
                    return {
                        ...stock,
                        cantidadFormateada: this.formatNumber(stock.cantidad) + " " + (this.product.unidad || "")
                    };
                });
        },
        sortedCurrentHistorico() {
            return [...this.historicoStock]
                .sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento))
                .map(historico => {
                    return {
                        ...historico,
                        cantidadFormateada: this.formatNumber(historico.cantidad) + " " + (this.product.unidad || "")
                    };
                });
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
                })
                .catch(error => {
                    console.error("Error al cargar stock actual:", error);
                });
        },
        addStock() {
            const params = new URLSearchParams(window.location.search);
            const idCarne = params.get('id');

            if (!idCarne) {
                this.showToast("Error: Producto no encontrado", "bg-danger");
                return;
            }
            // Valida que la fecha de ingreso sea menor que la fecha de vencimiento.
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
                    this.showToast(`${this.formatNumber(stockData.cantidad)} ${this.product.unidad} añadidas. Ingreso: ${this.formatDate(stockData.fechaIngreso)}, Vence: ${this.formatDate(stockData.fechaVencimiento)}`, "bg-primary");
                    this.loadCurrentStock();
                    this.newStock = { cantidad: '', fechaIngreso: '', fechaVencimiento: '' };
                })
                .catch(error => {
                    this.showToast("Error al agregar stock", "bg-danger");
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
            this.editingStock = JSON.parse(JSON.stringify(stock));
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
            // Se usa un solo contenedor de toast. Asegúrate de que el id sea el mismo en el HTML.
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
        // Formatea el número: si es entero no muestra decimales, sino los decimales necesarios.
        formatNumber(value) {
            const num = parseFloat(value);
            if (Number.isInteger(num)) {
                return num.toLocaleString('es-ES', { minimumFractionDigits: 0 });
            }
            return num.toLocaleString('es-ES', { minimumFractionDigits: 0, maximumFractionDigits: 3 });
        },
        openEditCarneModal() {
            this.editingCarne = { ...this.product };
            this.nombreError = '';
            this.unidadError = '';
            this.isInvalid = false;
            new bootstrap.Modal(document.getElementById("editCarneModal")).show();
        },
        validateEditCarne() {
            const nombre = this.editingCarne.nombre.trim().toLowerCase();
            const unidad = this.editingCarne.unidad.trim().toLowerCase();

            if (nombre === this.product.nombre.trim().toLowerCase() && unidad === this.product.unidad.trim().toLowerCase()) {
                this.nombreError = '';
                this.unidadError = '';
                this.isInvalid = false;
                return;
            }

            axios.get(`${VALIDAR_CARNE}?nombre=${encodeURIComponent(nombre)}&unidad=${encodeURIComponent(unidad)}`)
                .then(response => {
                    if (response.data.existe) {
                        this.nombreError = "Ya existe una carne con este nombre y unidad.";
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
        updateCarne() {
            if (this.isInvalid) return;

            axios.put(API_CARNE, this.editingCarne)
                .then(() => {
                    this.product = { ...this.editingCarne };
                    bootstrap.Modal.getInstance(document.getElementById("editCarneModal")).hide();
                    this.showToast("Carne actualizada correctamente.", "bg-success");
                })
                .catch(error => {
                    console.error("Error al actualizar carne:", error);
                    this.showToast("Error al actualizar la carne.", "bg-danger");
                });
        },
        // Métod0 para abrir el modal de eliminación
        openDeleteModal() {
            this.deleteModal = new bootstrap.Modal(document.getElementById("deleteCarneModal"));
            this.deleteModal.show();
        },
        deleteCarne() {
            // Se asume que el id del producto está en this.product.id
            console.log("Eliminando carne")
            axios.delete(API_CARNE + this.product.id)
                .then(() => {
                    this.showToast("Carne eliminada correctamente.", "bg-success");
                    // Redirigir a la lista de productos o a otro lugar
                    setTimeout(() => {
                        window.location.href = "../carne/gestion_carne.html";
                    }, 1500);
                })
                .catch(error => {
                    console.error("Error al eliminar la carne:", error);
                    this.showToast("Error al eliminar la carne.", "bg-danger");
                });
        },
    },
    mounted() {
        this.loadProductDetails();
        this.loadCurrentStock();
    }
}).mount("#app");
