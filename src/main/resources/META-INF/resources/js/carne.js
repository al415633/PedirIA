const RETRIEVE_ALL = "/carnes";
const POST = "/carnes/create";
const DELETE = "/carnes/delete/";
const RETRIEVE_ONE = "/carnes/retrieve/";
const UPDATE = "/carnes/update";

const STOCK_RETRIEVE_ONE = "/carnes/stock/retrieve/";
const STOCK_RETRIEVE = "/carnes/stock/producto/";
const STOCK_ADD = "/carnes/stock";
const STOCK_UPDATE = "/carnes/stock/";
const STOCK_DELETE = "/carnes/stock/";

Vue.createApp({
    data() {
        return {
            // Gestión productos
            carnes: [],
            nombreCarne: "",
            categoriaCarne: "",
            unidadCarne: "",
            tipoConserva: "",
            tiposConserva: ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO", "VIVO"], // Enum en frontend
            currentCarne: { id: null, nombre: "", categoria: "", unidad: "", tipoConserva: "" },

            // Gestión stock carne
            stocks: [],
            cantidadStock: 0,
            fechaIngresoStock: '',
            fechaVencimientoStock: '',
            mostrarStock: false,
            stockSeleccionado: {},
        };
    },
    methods: {
        // Obtiene el listado de carnes completo
        async doGet() {
            try {
                const response = await axios.get(RETRIEVE_ALL);
                this.carnes = response.data;
                console.log("Carnes cargadas:", response.data);
                this.currentCarne = { id: null, nombre: "", categoria: "", unidad: "", tipoConserva: "" };
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

        // Para cargar los datos de la carne a modificar
        async retrieveCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                console.log("Recuperando Carne con ID:", carneId);
                const response = await axios.get(RETRIEVE_ONE + carneId);
                this.currentCarne = response.data;
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

                await axios.post(POST, newCarne);
                console.log("Carne creada:", newCarne);

                // Actualiza el listado de carnes
                this.doGet();

                // Limpiar los campos después de crear
                this.nombreCarne = "";
                this.categoriaCarne = "";
                this.unidadCarne = "";
                this.tipoConserva = "";

            } catch (error) {
                console.error("Error al crear Carne:", error);
            }
        },

        // Se llama una vez se pulsa guardar en el modal de edición
        async updateCarne() {
            try {
                if (!this.currentCarne.id) {
                    console.error("Error: ID de carne no definido.");
                    return;
                }
                console.log("Actualizando Carne:", this.currentCarne);
                await axios.put(UPDATE, this.currentCarne);

                // Actualizar el listado de carnes
                this.doGet();

                // Cerrar el modal manualmente
                let modal = bootstrap.Modal.getInstance(document.getElementById('editModal'));
                if (modal) {
                    modal.hide();
                }
            } catch (error) {
                console.error("Error al actualizar Carne:", error);
            }
        },

        async deleteCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                console.log("Eliminando Carne con ID:", carneId);
                await axios.delete(DELETE + carneId);
                this.doGet();
            } catch (error) {
                console.error("Error al eliminar Carne:", error);
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

        async verStock(index) {
            try {
                const response = await axios.get(STOCK_RETRIEVE + this.carnes[index].id);
                console.log("Stock recibido:", response.data); // Agregar esta línea
                this.stocks = response.data;
                this.retrieveCarne(index)
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
                    carne: this.currentCarne,
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
                    idCarne: this.currentCarne.id,
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

        async eliminarStock() {
            if (!this.stockSeleccionado) {
                console.error("Error: No hay stock seleccionado para eliminar.");
                return;
            }
            try {
                await axios.delete(STOCK_DELETE + this.stockSeleccionado.id);
                this.stocks = this.stocks.filter(stock => stock.id !== this.stockSeleccionado.id);
                console.log("Stock eliminado:", this.stockSeleccionado);
                this.stockSeleccionado = null; // Limpiar la selección
            } catch (error) {
                console.error("Error al eliminar el stock:", error);
            }
        },


    },


    mounted() {
        this.doGet();
    }
}).mount("#app");
