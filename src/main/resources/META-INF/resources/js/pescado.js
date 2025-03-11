const RETRIEVE_ALL_PESCADOS = "/pescados";
const POST = "/pescados/create";
const DELETE = "/pescados/delete/";
const RETRIEVE_ONE_PESCADO = "/pescados/retrieve/";
const UPDATE = "/pescados/update";

const STOCK_RETRIEVE_ONE = "/pescados/stock/retrieve/";
const STOCK_RETRIEVE = "/pescados/stock/producto/";
const STOCK_ADD = "/pescados/stock";
const STOCK_UPDATE = "/pescados/stock/";
const STOCK_DELETE = "/pescados/stock/";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "VIVO"];


Vue.createApp({
    data() {
        return {

            // Gestión de los pescados
            pescados: [],
            nombrePescado: "",
            categoriaPescado: "",
            unidadPescado: "",
            tipoConserva: "",
            pescadoSeleccionado: {},

            // Gestión stock
            stocks: [],
            cantidadStock: 0,
            fechaIngresoStock: '',
            fechaVencimientoStock: '',
            mostrarStock: false,
            stockSeleccionado: {},
        };
    },

    // Permite hacer visible la enumeración constante    
    computed: {
        tiposConserva() {
            return TIPOS_CONSERVA;
        }
    },

    methods: {

        // Obtiene el listado de pescados completo
        async getAllPescados() {
            try {
                const response = await axios.get(RETRIEVE_ALL_PESCADOS);
                this.pescados = response.data;

                // Ordenar los pescados
                this.pescados.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                console.log("Pescados cargados:", response.data);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

        // Para cargar los datos del pescado a modificar
        async retrievePescado(index) {
            try {
                const pescadoId = this.pescados[index].id;
                console.log("Recuperando Pescado con ID:", pescadoId);
                const response = await axios.get(RETRIEVE_ONE_PESCADO + pescadoId);
                this.pescadoSeleccionado = response.data;
            } catch (error) {
                console.error("Error al recuperar el pescado:", error);
            }
        },

        async createPescado() {
            try {
                let newPescado = {
                    nombre: this.nombrePescado,
                    categoria: this.categoriaPescado,
                    unidad: this.unidadPescado,
                    tipoConserva: this.tipoConserva
                };

                const response = await axios.post(POST, newPescado);
                console.log("Pescado creado:", newPescado);

                // Agregar el nuevo pescado al listado localmente
                console.log("Pescado devuelto por API:", response.data);
                this.pescados.push(response.data); // Suponiendo que la respuesta contiene el objeto creado

                // Ordenar los pescados
                this.pescados.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                // Limpiar los campos después de crear
                this.nombrePescado = "";
                this.categoriaPescado = "";
                this.unidadPescado = "";
                this.tipoConserva = "";

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = '¡Pescado añadido con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al crear Pescado:", error);
            }
        },

        // Se llama una vez se pulsa guardar en el modal de edición
        async updatePescado() {
            try {
                if (!this.pescadoSeleccionado.id) {
                    console.error("Error: ID de pescado no definido.");
                    return;
                }
                console.log("Actualizando Pescado:", this.pescadoSeleccionado);

                // Mostrar un mensaje de carga
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Actualizando pescado...'; // Mensaje de carga
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

                // Realizar la solicitud de actualización
                const response = await axios.put(UPDATE, this.pescadoSeleccionado);

                // Verificar si la respuesta es exitosa
                if (response.status === 204) { // 204 No Content indica éxito, lógica en el resource
                    // Actualizar el listado de pescados localmente
                    const index = this.pescados.findIndex(pescado => pescado.id === this.pescadoSeleccionado.id);
                    if (index !== -1) {
                        this.pescados.splice(index, 1, { ...this.pescadoSeleccionado }); // Reemplaza el elemento modificado
                    }

                    // Ordenar los pescados después de la actualización
                    this.pescados.sort((a, b) => a.nombre.localeCompare(b.nombre));

                    // Cerrar el modal manualmente
                    let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                    if (modal) {
                        modal.hide();
                    }

                    // Mostrar el toast de éxito
                    toastBody.textContent = '¡Pescado actualizada con éxito!'; // Mensaje de éxito
                    toastEl.classList.add('bg-success'); // Cambiar el color de fondo a verde
                    toast.show();
                } else {
                    // Lanzar una excepción si la respuesta no es 204
                    throw new Error('Error inesperado al actualizar el pescado. Código de estado: ' + response.status);
                }
            } catch (error) {
                console.error("Error al actualizar el pescado:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al actualizar el pescado. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },
        // Llama al modal para confirmar el borrado
        async deletePescado(index) {
            // Mostrar el modal de confirmación
            const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
            modal.show();

            // Asignar el índice a un atributo del modal para usarlo en la confirmación
            const confirmButton = document.getElementById('confirmDeleteButton');
            confirmButton.onclick = () => {
                // Cerrar el modal aquí para evitar problemas
                modal.hide();
                this.confirmDelete(index);
            };
        },

        // Se encarga de comunicar con backend y eliminar el pescado
        async confirmDelete(index) {
            // Verificar que el índice sea válido
            if (index < 0 || index >= this.pescados.length) {
                console.error("Índice de pescado no válido:", index);
                return;
            }

            try {
                const pescadoId = this.pescados[index].id;
                console.log("Eliminando Pescado con ID:", pescadoId);
                await axios.delete(DELETE + pescadoId);

                // Añadir comprobación de que se borra correctamente

                // Eliminar del listado localmente
                this.pescados.splice(index, 1);

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = '¡Pescado eliminada con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al eliminar Pescado:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al eliminar el pescado. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },

        // Métodos de gestión del stock

        // Obtiene el listado de stock para la carne seleccionada
        async getStockByProduct(productId) {
            try {
                const response = await axios.get(STOCK_RETRIEVE + productId);
                this.stocks = response.data;
            } catch (error) {
                console.error("Error al obtener el stock:", error);
            }
        },

        // Para seleccionar una carne y cargar su stock
        async retrieveStockandProduct(index) {
            try {
                const productID = this.pescados[index].id;
                const response = await axios.get(RETRIEVE_ONE_PESCADO + productID);
                this.pescadoSeleccionado = response.data;
                console.log("Pescado obtenido: ", response.data)

                await this.getStockByProduct(productID);
                this.mostrarStock = true;

            } catch (error) {
                console.error("Error al cargar el producto el stock:", error);
                this.mostrarStock = false;
            }
        },

        // Se emplea para agregar stock
        async agregarStock() {
            try {
                console.log("Pescado seleccionado: ", this.pescadoSeleccionado)

                const nuevoStock = {
                    cantidad: this.cantidadStock,
                    fechaIngreso: this.fechaIngresoStock,
                    fechaVencimiento: this.fechaVencimientoStock,
                    pescado: this.pescadoSeleccionado
                };

                const response = await axios.post(STOCK_ADD, nuevoStock);

                this.stocks.push(response.data);

                // Ordenar con algún criterio

                this.cantidadStock = 0;
                this.fechaIngresoStock = '';
                this.fechaVencimientoStock = '';

                // Mostrar mensaje de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = '¡Stock agregado con éxito!';
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al agregar stock:", error);
            }
        },

        // Para eliminar stock
        async eliminarStock(index) {
            const stockId = this.stocks[index].id;
            try {
                await axios.delete(STOCK_DELETE + stockId);
                this.stocks.splice(index, 1);

                // Mostrar mensaje de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = '¡Stock eliminado con éxito!';
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al eliminar stock:", error);
            }
        },
    },

    mounted() {
        this.getAllPescados();
    }
}).mount("#app");
