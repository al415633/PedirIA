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
            offerStockData: { id: null, cantidad: '', fechaIngreso: '', fechaVencimiento: ''},
            historicoStock: [],
            activeTab: "stock",
            editingCarne: { nombre: '', unidad: '', tipoConserva: '' },
            tiposConserva: ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"],
            nombreError: '',
            unidadError: '',
            isInvalid: false,
            ofertas: [],
            ofertasAceptadas: [],
            nuevaOferta: {
                ubicacion: '',
                cantidad: '',
            },
            editingOferta: { ubicacion: '' },
            selectedOffer: {},
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
                    console.log(this.ofertas)

                    // Ordenar las ofertas por fechaBaja (fecha de vencimiento)
                    this.ofertas.sort((a, b) => {
                        // Asegúrate de que las fechas están en formato Date (si no lo están, conviértelas)
                        const fechaA = new Date(a.productoOferta.stock.fechaVencimiento);
                        const fechaB = new Date(b.productoOferta.stock.fechaVencimiento);

                        // Orden ascendente (de menor a mayor fecha de vencimiento)
                        return fechaA - fechaB;
                    });

                    this.activeTab = "ofertasPublicadas"
                })
                .catch(error => {
                    console.error("Error al cargar ofertas:", error);
                });
        },
        loadOfertasAceptadas() {
            axios.get(API_OFERTAS + "/mis-ofertas-aceptadas/carne/" + this.product.id)
                .then(response => {
                    this.ofertasAceptadas = response.data;
                    console.log(this.ofertasAceptadas)

                    // Ordenar las ofertas por fechaBaja (fecha de vencimiento)
                    this.ofertasAceptadas.sort((a, b) => {
                        // Asegúrate de que las fechas están en formato Date (si no lo están, conviértelas)
                        const fechaA = new Date(a.productoOferta.stock.fechaVencimiento);
                        const fechaB = new Date(b.productoOferta.stock.fechaVencimiento);

                        // Orden ascendente (de menor a mayor fecha de vencimiento)
                        return fechaA - fechaB;
                    });

                    this.activeTab = "ofertasAceptadas"
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
            console.log(this.editingStock)
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
        openDeleteModal() {
            this.deleteModal = new bootstrap.Modal(document.getElementById("deleteCarneModal"));
            this.deleteModal.show();
        },
        deleteCarne() {
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
        openCreateOfferModal(stock) {
            this.nuevaOferta.stockId = stock.id;
            this.nuevaOferta.cantidad = '';
            this.nuevaOferta.ubicacion = '';
            this.nuevaOferta.unidad = this.product.unidad;
            new bootstrap.Modal(document.getElementById("createOfertaModal")).show();
        },
        crearOferta() {
            const currentDate = new Date().toISOString().split('T')[0]; // Formato 'yyyy-mm-dd'

            // Construimos la petición, que coincide con la estructura de OfertaRequest (oferta y stock)
            const ofertaRequest = {
                ubicacion: this.nuevaOferta.ubicacion,
                cantidad: parseInt(this.nuevaOferta.cantidad),
                fechaAlta: new Date().toISOString().split('T')[0],
                fechaBaja: null,
                idStock: this.nuevaOferta.stockId,
                tipoStock: "Carne"

            };

            axios.post("/oferta", ofertaRequest)
                .then(response => {
                    this.showToast("Oferta creada con éxito", "bg-success");
                    bootstrap.Modal.getInstance(document.getElementById("createOfertaModal")).hide();
                    this.loadCurrentStock();
                })
                .catch(error => {
                    this.showToast(error.response.data, "bg-danger");
                    console.error("Error:", error);
                });
        },
        editOferta(oferta) {
            this.editingOferta = JSON.parse(JSON.stringify(oferta));
            new bootstrap.Modal(document.getElementById("editOfertaModal")).show();
        },
        updateOferta() {
            axios.put(API_OFERTAS + "/" + this.editingOferta.id, this.editingOferta)
                .then(() => {
                    bootstrap.Modal.getInstance(document.getElementById("editOfertaModal")).hide();
                    this.loadProductDetails();
                    this.loadCurrentStock();
                    this.loadOfertasPublicadas();
                    this.showToast("Oferta modificada con éxito", "bg-success");
                })
                .catch(error => {
                    this.showToast(error.response.data, "bg-danger");
                    console.error("Error:", error);
                });

        },
        openDeleteOfferModal(oferta) {
            this.selectedOffer = JSON.parse(JSON.stringify(oferta));
            new bootstrap.Modal(document.getElementById("deleteOfferModal")).show();
        },
        deleteOferta() {
            axios.delete(API_OFERTAS + "/" + this.selectedOffer.id)
                .then(() => {
                    this.showToast("Oferta eliminada con éxito.", "bg-success");
                    // Cerrar el modal
                    bootstrap.Modal.getInstance(document.getElementById("deleteOfferModal")).hide();
                    // Recargar la lista de ofertas para reflejar la eliminación
                    this.loadOfertasPublicadas();
                })
                .catch(error => {
                    console.error("Error al eliminar la oferta:", error);
                    this.showToast("Error al eliminar la oferta.", "bg-danger");
                });
        },
        isNearExpiration(fechaVencimiento) {
            const currentDate = new Date();
            const expirationDate = new Date(fechaVencimiento);

            // Calcular la diferencia en días entre la fecha actual y la fecha de vencimiento
            const timeDiff = expirationDate - currentDate;
            const daysLeft = Math.ceil(timeDiff / (1000 * 3600 * 24)); // Convertir ms a días

            return daysLeft <= 4; // Si está dentro de 4 días o menos
        }
    },
    mounted() {
        this.loadProductDetails();
        this.loadCurrentStock();
    }
}).mount("#app");
