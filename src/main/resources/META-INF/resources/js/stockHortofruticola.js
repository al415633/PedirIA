const { createApp } = Vue;
const RETRIEVE_ONE_HORTOFRUTICOLA = "/hortofruticolas/";
const STOCK_RETRIEVE_ONE = "/hortofruticolas/stock/producto/";
const ADD_STOCK = "/hortofruticolas/stock";
const API_STOCK = "/hortofruticolas/stock/";
const API_HISTORICO = "/hortofruticolas/stock/historico/";
const API_HORTOFRUTICOLA = "/hortofruticolas/";
const VALIDAR_HORTOFRUTICOLA = "/hortofruticolas/validar";

const API_OFERTAS = "/oferta";

const RETRIEVE_ACTIVE = "/comercio/obtener";
const LOGOUT = "/comercio/logout";
const DELETE = "/comercio/delete";


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
            isInvalid: false,
            ofertas: [],
            ofertasAceptadas: [],
            nuevaOferta: {
                ubicacion: '',
                cantidad: '',
            },
            editingOferta: { ubicacion: '' },
            selectedOffer: {},

            currentPageStock: 1,
            currentPageHistorico: 1,
            itemsPerPage: 7,
        };
    },
    computed: {
        sortedCurrentStockPaginated() {
            const start = (this.currentPageStock - 1) * this.itemsPerPage;
            const sortedStock = [...this.currentStock].sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));
            return sortedStock.slice(start, start + this.itemsPerPage).map(stock => {
                return {
                    ...stock,
                    cantidadFormateada: this.formatNumber(stock.cantidad) + " " + (this.product.unidad || "")
                };
            });
        },

        sortedCurrentHistoricoPaginated() {
            const start = (this.currentPageHistorico - 1) * this.itemsPerPage;
            const sortedHistorico = [...this.historicoStock].sort((a, b) => new Date(a.fechaVencimiento) - new Date(b.fechaVencimiento));
            return sortedHistorico.slice(start, start + this.itemsPerPage).map(historico => {
                return {
                    ...historico,
                    cantidadFormateada: this.formatNumber(historico.cantidad) + " " + (this.product.unidad || "")
                };
            });
        },

        totalPagesStock() {
            return Math.ceil(this.currentStock.length / this.itemsPerPage);
        },

        totalPagesHistorico() {
            return Math.ceil(this.historicoStock.length / this.itemsPerPage);
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
            axios.get(API_OFERTAS + "/mis-ofertas-publicadas/hortofruticola/" + this.product.id)
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
            axios.get(API_OFERTAS + "/mis-ofertas-aceptadas/hortofruticola/" + this.product.id)
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
                    this.showToast("Hortofruticola actualizada correctamente.", "bg-success");
                })
                .catch(error => {
                    console.error("Error al actualizar hortofruticola:", error);
                    this.showToast("Error al actualizar la hortofruticola.", "bg-danger");
                });
        },

        showToast(message, bgClass) {
            this.toastMessage = message;
            const toastEl = document.getElementById("editToast");
            toastEl.className = `toast custom-toast text-white ${bgClass} show`;
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        },
        // Métod0 para abrir el modal de eliminación
        openDeleteModal() {
            this.deleteModal = new bootstrap.Modal(document.getElementById("deleteHortofruticolaModal"));
            this.deleteModal.show();
        },
        deleteHortofruticola() {
            axios.delete(API_HORTOFRUTICOLA + this.product.id)
                .then(() => {
                    this.showToast("Producto eliminada correctamente.", "bg-success");
                    // Redirigir a la lista de productos o a otro lugar
                    setTimeout(() => {
                        window.location.href = "../hortofruticola/gestion_hortofruticola.html";
                    }, 1500);
                })
                .catch(error => {
                    console.error("Error al eliminar el hortofruticola:", error);
                    this.showToast("Error al eliminar el hortofruticola.", "bg-danger");
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
                tipoStock: "Hortofruticola"

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

        toggleUserDropdown() {
            this.showUserDropdown = !this.showUserDropdown;
        },

        // Mostrar el modal para confirmar la acción de cerrar sesión
        confirmLogout() {
            //console.log("confirm 1");
            //this.modalMessage = '¿Estás seguro que quieres cerrar sesión?';
            this.actionToConfirm = this.logoutComercio();  // Establecer la acción que se confirmará
            //this.showModal = true;  // Mostrar el modal

        },
        async deleteComercio() {
            console.log("llego al delete")
            // Construimos la URL para el DELETE con el correo como parámetro
            const url = `${DELETE}`;

            const confirmacion = window.confirm('¿Estás seguro que quieres eliminar tu cuenta?');
            if (confirmacion) {
                try {
                    // Realizamos la solicitud DELETE al backend
                    const response = await axios.delete(url);

                    // Verificamos si la eliminación fue exitosa
                    if (response.status === 204) {
                        alert("Comercio eliminado con éxito");
                        window.location.href = "../index.html"; // Redirigir a otra página si es necesario
                    } else {
                        throw new Error("Error en la eliminación");
                    }

                } catch (error) {
                    console.error("Error al eliminar comercio:", error);
                    alert("Hubo un problema con la eliminación del comercio.");
                    window.location.href = "registroError.html"; // Redirigir en caso de error
                }
            }}

        ,
        async logoutComercio() {
            try {
                console.log("logout 1");

                const confirmacion = window.confirm('¿Estás seguro que quieres cerrar sesión?');
                if (confirmacion) {
                    // Realizamos la solicitud POST al backend para cerrar sesión
                    const response = await axios.post(LOGOUT);

                    // Verificamos si la respuesta fue exitosa
                    if (response.status === 200) {
                        alert("Sesión cerrada con éxito");
                        window.location.href = "../index.html"; // Redirigir al login o a la página deseada
                    } else {
                        throw new Error("Error al cerrar sesión");
                    }
                }
            } catch (error) {
                console.error("Error al cerrar sesión:", error);
                alert("Hubo un problema al cerrar sesión.");
                window.location.href = "registroError.html"; // Redirigir en caso de error
            }


        },
        async getActiveComercio() {
            await axios.get(RETRIEVE_ACTIVE)
                .then((response) => {
                    this.usuarioActivo = response.data;
                    console.log(response.data);
                })
                .catch((error) => {
                    console.log("Error al obtener los datos:", error);
                });
        },

        // Mostrar el modal para confirmar la eliminación de cuenta
        confirmDeleteAccount() {
            this.modalMessage = '¿Estás seguro que quieres eliminar tu cuenta?';
            this.actionToConfirm = this.deleteComercio();  // Establecer la acción que se confirmará
            //this.showModal = true;  // Mostrar el modal
        },

        // Ejecutar la acción confirmada (cerrar sesión o eliminar cuenta)
        async confirmAction() {
            if (this.actionToConfirm) {
                await this.actionToConfirm();  // Ejecutar la acción confirmada
            }
            this.closeModal();  // Cerrar el modal después de la acción
        },

        // Cerrar el modal sin hacer nada
        closeModal() {
            this.showModal = false;
            this.actionToConfirm = null;
        },

    },
    mounted() {
        this.loadProductDetails();
        this.loadCurrentStock();
        this.getActiveComercio()
    }
}).mount("#app");