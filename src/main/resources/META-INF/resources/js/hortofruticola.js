const RETRIEVE_ALL_HORTOFRUTICOLAS = "/hortofruticolas";
const POST = "/hortofruticolas/create";
const DELETE = "/hortofruticolas/delete/";
const RETRIEVE_ONE_HORTOFRUTICOLA = "/hortofruticolas/retrieve/";
const UPDATE = "/hortofruticolas/update";

const STOCK_RETRIEVE_ONE = "/hortofruticolas/stock/retrieve/";
const STOCK_RETRIEVE = "/hortofruticolas/stock/producto/";
const STOCK_ADD = "/hortofruticolas/stock";
const STOCK_UPDATE = "/hortofruticolas/stock/";
const STOCK_DELETE = "/hortofruticolas/stock/";


const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO", "VIVO"];

Vue.createApp({
    data() {
        return {
            // Gestión de los hortofrutícolas
            hortofruticolas: [],
            nombreHortoFruticola: "",
            unidadHortoFruticola: "",
            tipoConserva: "",
            hortoFruticolaSeleccionado: {},
        };
    },

    // Permite hacer visible la enumeración constante
    computed: {
        tiposConserva() {
            return TIPOS_CONSERVA;
        }
    },

    methods: {
        // Obtiene el listado de hortofrutícolas completo
        async getAllHortoFruticolas() {
            try {
                const response = await axios.get(RETRIEVE_ALL_HORTOFRUTICOLAS);
                this.hortofruticolas = response.data;

                // Ordenar los hortofrutícolas después
                this.hortofruticolas.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                console.log("Hortofrutícolas cargados:", response.data);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

        // Para cargar los datos del hortofrutícola a modificar
        async retrieveHortoFruticola(index) {
            try {
                const hortoFruticolaId = this.hortofruticolas[index].id;
                console.log("Recuperando HortoFruticola con ID:", hortoFruticolaId);
                const response = await axios.get(RETRIEVE_ONE_HORTOFRUTICOLA + hortoFruticolaId);
                this.hortoFruticolaSeleccionado = response.data;
            } catch (error) {
                console.error("Error al recuperar el HortoFruticola:", error);
            }
        },

        async createHortoFruticola() {
            try {
                let newHortoFruticola = {
                    nombre: this.nombreHortoFruticola,
                    unidad: this.unidadHortoFruticola,
                    tipoConserva: this.tipoConserva
                };

                const response = await axios.post(POST, newHortoFruticola);
                console.log("HortoFruticola creado:", newHortoFruticola);

                // Agregar el nuevo hortofrutícola al listado localmente
                console.log("HortoFruticola devuelto por API:", response.data);
                this.hortofruticolas.push(response.data); // Suponiendo que la respuesta contiene el objeto creado

                // Ordenar los hortofrutícolas después
                this.hortofruticolas.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                // Limpiar los campos después de crear
                this.nombreHortoFruticola = "";
                this.unidadHortoFruticola = "";
                this.tipoConserva = "";

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'HortoFruticola añadido con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastHortoFruticola');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al crear HortoFruticola:", error);
            }
        },

        // Se llama una vez se pulsa guardar en el modal de edición
        async updateHortoFruticola() {
            try {
                if (!this.hortoFruticolaSeleccionado.id) {
                    console.error("Error: ID de horto-frutícola no definido.");
                    return;
                }
                console.log("Actualizando HortoFruticola:", this.hortoFruticolaSeleccionado);

                // Mostrar un mensaje de carga
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Actualizando hortofruticola...'; // Mensaje de carga
                const toastEl = document.getElementById('toastHortofruticola');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

                // Realizar la solicitud de actualización
                const response = await axios.put(UPDATE, this.hortoFruticolaSeleccionado);

                // Verificar si la respuesta es exitosa
                if (response.status === 204) { // 204 No Content indica éxito, lógica en el resource
                    // Actualizar el listado de hortofruticolas localmente
                    const index = this.hortofruticolas.findIndex(hortofruticola => hortofruticola.id === this.hortoFruticolaSeleccionado.id);
                    if (index !== -1) {
                        this.hortofruticolas.splice(index, 1, { ...this.hortoFruticolaSeleccionado }); // Reemplaza el elemento modificado
                    }

                    // Ordenar las hortofruticolas después de la actualización
                    this.hortofruticolas.sort((a, b) => a.nombre.localeCompare(b.nombre));

                    // Cerrar el modal manualmente
                    let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                    if (modal) {
                        modal.hide();
                    }

                    // Mostrar el toast de éxito
                    toastBody.textContent = 'Hortofruticola actualizada con éxito!'; // Mensaje de éxito
                    toastEl.classList.add('bg-success'); // Cambiar el color de fondo a verde
                    toast.show();
                } else {
                    // Lanzar una excepción si la respuesta no es 204
                    throw new Error('Error inesperado al actualizar la hortofruticola. Código de estado: ' + response.status);
                }
            } catch (error) {
                console.error("Error al actualizar hortofruticola:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al actualizar la hortofruticola. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastHortofruticola');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },
        // Llama al modal para confirmar el borrado
        deleteHortofruticola(index) {
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

        // Se encarga de comunicar con backend y eliminar la hortofruticola
        async confirmDelete(index) {
            // Verificar que el índice sea válido
            if (index < 0 || index >= this.hortofruticolas.length) {
                console.error("Índice de hortofruticola no válido:", index);
                return;
            }

            try {
                const hortofruticolaId = this.hortofruticolas[index].id; // Obtener el ID de la hortofruticola
                console.log("Eliminando hortofruticola con ID:", hortofruticolaId);
                await axios.delete(DELETE + hortofruticolaId);

                console.log("Eliminada de la BBDD");
                // Eliminar la hortofruticola del listado localmente
                this.hortofruticolas.splice(index, 1);

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Hortofruticola eliminada con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastHortofruticola');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al eliminar hortofruticola:", error);
                // Mostrar el toast de error
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Ocurrió un error al eliminar la hortofruticola. Por favor, intenta nuevamente.';
                const toastEl = document.getElementById('toastHortofruticola');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            }
        },

        // Métodos de gestión del stock

        // Obtiene el listado de stock para la hortofruticola seleccionada
        async getStockByHortofruticola(hortofruticolaId) {
            try {
                const response = await axios.get(STOCK_RETRIEVE + hortofruticolaId);
                this.stocks = response.data;
            } catch (error) {
                console.error("Error al obtener el stock:", error);
            }
        },

        // Para seleccionar una hortofruticola y cargar su stock
        async retrieveStockandHortofruticola(index) {
            try {
                const hortofruticolaId = this.hortofruticolas[index].id;
                console.log("Seleccionando Hortofruticola con ID para mostrar stock:", hortofruticolaId);
                const response = await axios.get(RETRIEVE_ONE_HORTOFRUTICOLA + hortofruticolaId);
                this.hortofruticolas = response.data;

                // Obtener el stock de la hortofruticola seleccionada
                await this.getStockByHortofruticola(hortofruticolaId);
                console.log("Hortofruticola para mostrar stock:", this.hortofruticolas);
                console.log("Hortofruticolas cargadas: ", this.hortofruticolas)
                this.mostrarStock = true; // Mostrar la sección de stock

            } catch (error) {
                console.error("Error al cargar la hortofruticola o el stock:", error);
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
                    hortofruticola: this.hortofruticolas
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
                const toastEl = document.getElementById('toastHortofruticola');
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
                const toastEl = document.getElementById('toastHortofruticola');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al eliminar stock:", error);
            }
        },

    },


    mounted() {
        this.getAllHortoFruticolas();
    }
}).mount("#app");
