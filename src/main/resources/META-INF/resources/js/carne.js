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
                await axios.put(UPDATE, this.carneSeleccionada);

                console.log("Actualizando correctamente");

                // Actualizar el listado de carnes localmente
                const index = this.carnes.findIndex(carne => carne.id === this.carneSeleccionada.id);
                if (index !== -1) {
                    this.carnes.splice(index, 1, { ...this.carneSeleccionada }); // Reemplaza el elemento modificado
                }

                // Ordenar las carnes después de la actualización
                this.carnes.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                // Cerrar el modal manualmente
                let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                if (modal) {
                    modal.hide();
                }

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = 'Carne actualizada con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastCarne');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();
            } catch (error) {
                console.error("Error al actualizar Carne:", error);
            }
        },

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

        async retrieveStock(index) {
            try {
                const stockId = this.stocks[index].id;
                const response = await axios.get(STOCK_RETRIEVE_ONE + stockId);
                this.stockSeleccionado = response.data;
            } catch (error) {
                console.error("Error al recuperar el Stock de la carne:", error);
            }
        },

        async verStock(carneID) {
            try {
                const response = await axios.get(STOCK_RETRIEVE + carneID);
                console.log("Stock recibido:", response.data); // Agregar esta línea
                this.stocks = response.data;
                this.mostrarStock = true;
            } catch (error) {
                console.error("Error al obtener el stock:", error);
            }
        },

        async agregarStock() {
            try {
                const nuevoStock = {
                    cantidad: this.cantidadStock,
                    fechaIngreso: this.fechaIngresoStock,
                    fechaVencimiento: this.fechaVencimientoStock,
                    carne: this.carneSeleccionada,
                };

                console.log("Stock a añadir: ", nuevoStock)
                await axios.post(STOCK_ADD, nuevoStock);

                this.stocks.push(nuevoStock);
                this.cantidadStock = 0;
                this.fechaIngresoStock = '';
                this.fechaVencimientoStock = '';
            } catch (error) {
                console.error("Error al agregar stock:", error);
            }
        },

        async modificarStock() {
            if (!this.stockSeleccionado) {
                console.error("Error: No hay stock seleccionado para modificar.");
                return;
            }
            try {
                const updatedStock = {
                    cantidad: this.stockSeleccionado.cantidad,
                    fechaIngreso: this.stockSeleccionado.fechaIngreso,
                    fechaVencimiento: this.stockSeleccionado.fechaVencimiento,
                    idCarne: this.carneSeleccionada.id,
                };
                await axios.put(STOCK_UPDATE + this.stockSeleccionado.id, updatedStock);
                const index = this.stocks.findIndex(stock => stock.id === this.stockSeleccionado.id);
                if (index !== -1) {
                    this.stocks[index] = updatedStock; // Actualiza el stock en el frontend
                }
                console.log("Stock modificado:", updatedStock);
            } catch (error) {
                console.error("Error al modificar el stock:", error);
            }
        },

        async eliminarStock(index) {

            let stockEliminado;
            try {
                console.log("Id de la carne a eliminar: ", this.stocks[index].id)
                stockEliminado = this.stocks[index].id
                await axios.delete(STOCK_DELETE + this.stocks[index].id);
                // Esto no actualiza la lista
                // this.stocks = this.stocks.filter(stock => stock.id !== stockEliminado.id);
                this.verStock(stockEliminado.carne.id)
            } catch (error) {
                console.error("Error al eliminar el stock:", error);
            }
        },


    },


    mounted() {
        this.getAllCarnes();
    }
}).mount("#app");
