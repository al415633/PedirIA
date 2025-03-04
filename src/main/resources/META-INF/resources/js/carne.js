const RETRIEVE_ALL_CARNES = "/carnes";
const POST = "/carnes/create";
const DELETE = "/carnes/delete/";
const RETRIEVE_ONE_CARNE = "/carnes/retrieve/";
const UPDATE = "/carnes/update";

const STOCK_RETRIEVE_ONE = "/carnes/stock/retrieve/";
const STOCK_RETRIEVE = "/carnes/stock/producto/";
const STOCK_ADD = "/carnes/stock";
const STOCK_UPDATE = "/carnes/stock/";
const STOCK_DELETE = "/carnes/stock/";
const STOCK_PREDICT = "/carnes/stock/predict";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO", "VIVO"];


    Vue.createApp({
    data() {
        return {

            // Gestión de las carnes
            carnes: [],
            nombreCarne: "",
            categoriaCarne: "",
            unidadCarne: "",
            tipoConserva: "",
            carneSeleccionada: {},

            // Gestión stock carne
            stocks: [],
            cantidadStock: 0,
            fechaIngresoStock: '',
            fechaVencimientoStock: '',
            mostrarStock: false,
            stockSeleccionado: {},
            stockPrediction: '',
        };
    },
        
    // Permite hacer visible la enumeración constante    
    computed: {
        tiposConserva() {
            return TIPOS_CONSERVA;
        }
    },

    methods: {

        // Obtiene el listado de carnes completo
        async getAllCarnes() {
            try {
                const response = await axios.get(RETRIEVE_ALL_CARNES);
                this.carnes = response.data;

                // Ordenar las carnes después
                this.carnes.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                console.log("Carnes cargadas:", response.data);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

        // Para cargar los datos de la carne a modificar
        async retrieveCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                console.log("Recuperando Carne con ID:", carneId);
                const response = await axios.get(RETRIEVE_ONE_CARNE + carneId);
                this.carneSeleccionada = response.data;
            } catch (error) {
                console.error("Error al recuperar la Carne:", error);
            }
        },

        async createCarne() {
            try {
                let newCarne = {
                    nombre: this.nombreCarne,
                    categoria: this.categoriaCarne,
                    unidad: this.unidadCarne,
                    tipoConserva: this.tipoConserva
                };

                const response = await axios.post(POST, newCarne);
                console.log("Carne creada:", newCarne);

                // Agregar la nueva carne al listado localmente
                console.log("Carne devuelta por API:", response.data);
                this.carnes.push(response.data); // Suponiendo que la respuesta contiene el objeto creado

                // Ordenar las carnes después
                this.carnes.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                // Limpiar los campos después de crear
                this.nombreCarne = "";
                this.categoriaCarne = "";
                this.unidadCarne = "";
                this.tipoConserva = "";

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Carne añadida con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al crear Carne:", error);
            }
        },

        // Se llama una vez se pulsa guardar en el modal de edición
        async updateCarne() {
            try {
                if (!this.carneSeleccionada.id) {
                    console.error("Error: ID de carne no definido.");
                    return;
                }
                console.log("Actualizando Carne:", this.carneSeleccionada);

                // Mostrar un mensaje de carga
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Actualizando carne...'; // Mensaje de carga
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

                // Realizar la solicitud de actualización
                const response = await axios.put(UPDATE, this.carneSeleccionada);

                // Verificar si la respuesta es exitosa
                if (response.status === 204) { // 204 No Content indica éxito, lógica en el resource
                    // Actualizar el listado de carnes localmente
                    const index = this.carnes.findIndex(carne => carne.id === this.carneSeleccionada.id);
                    if (index !== -1) {
                        this.carnes.splice(index, 1, { ...this.carneSeleccionada }); // Reemplaza el elemento modificado
                    }

                    // Ordenar las carnes después de la actualización
                    this.carnes.sort((a, b) => a.nombre.localeCompare(b.nombre));

                    // Cerrar el modal manualmente
                    let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                    if (modal) {
                        modal.hide();
                    }

                    // Mostrar el toast de éxito
                    toastBody.textContent = 'Carne actualizada con éxito!'; // Mensaje de éxito
                    toastEl.classList.add('bg-success'); // Cambiar el color de fondo a verde
                    toast.show();
                } else {
                    // Lanzar una excepción si la respuesta no es 204
                    throw new Error('Error inesperado al actualizar la carne. Código de estado: ' + response.status);
                }
            } catch (error) {
                console.error("Error al actualizar Carne:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al actualizar la carne. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },
        // Llama al modal para confirmar el borrado
        deleteCarne(index) {
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

        // Se encarga de comunicar con backend y eliminar la carne
        async confirmDelete(index) {
            // Verificar que el índice sea válido
            if (index < 0 || index >= this.carnes.length) {
                console.error("Índice de carne no válido:", index);
                return;
            }

            try {
                const carneId = this.carnes[index].id; // Obtener el ID de la carne
                console.log("Eliminando Carne con ID:", carneId);
                await axios.delete(DELETE + carneId);

                console.log("Eliminada de la BBDD");
                // Eliminar la carne del listado localmente
                this.carnes.splice(index, 1);

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Carne eliminada con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al eliminar Carne:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al eliminar la carne. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },

     // Métodos de gestión del stock

        // Obtiene el listado de stock para la carne seleccionada
        async getStockByCarne(carneId) {
            try {
                const response = await axios.get(STOCK_RETRIEVE + carneId);
                this.stocks = response.data;
            } catch (error) {
                console.error("Error al obtener el stock:", error);
            }
        },

        // Para seleccionar una carne y cargar su stock
        async retrieveStockandCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                console.log("Seleccionando Carne con ID para mostrar stock:", carneId);
                const response = await axios.get(RETRIEVE_ONE_CARNE + carneId);
                this.carneSeleccionada = response.data;

                // Obtener el stock de la carne seleccionada
                await this.getStockByCarne(carneId);
                console.log("Carne para mostrar stock:", this.carneSeleccionada);
                console.log("Carnes cargadas: ", this.carnes)
                this.mostrarStock = true; // Mostrar la sección de stock

            } catch (error) {
                console.error("Error al cargar la carne o el stock:", error);
                this.mostrarStock = false; // Asegúrate de ocultar el stock si hay un error
            }
        },

        // Se emplea para agregar stock
        async agregarStock() {
            try {
                const nuevoStock = {
                    cantidad: this.cantidadStock,
                    fechaIngreso: this.fechaIngresoStock,
                    fechaVencimiento: this.fechaVencimientoStock,
                    carne: this.carneSeleccionada
                };

                const response = await axios.post(STOCK_ADD, nuevoStock);

                this.stocks.push(response.data);

                // Ordenar con algún criterio

                this.cantidadStock = 0;
                this.fechaIngresoStock = '';
                this.fechaVencimientoStock = '';

                // Mostrar mensaje de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Stock agregado con éxito!';
                const toastEl = document.getElementById('toastCarne');
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
                toastBody.textContent = 'Stock eliminado con éxito!';
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al eliminar stock:", error);
            }
        },

        // Para pedir una prediccion
        async predecirStock() {
            try {
                const response = await axios.get(STOCK_PREDICT);
                this.stockPrediction = response.data;

                console.log("predicción cargada:", response.data);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

    },


    mounted() {
        this.getAllCarnes();
    }
}).mount("#app");
