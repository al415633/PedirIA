const RETRIEVE_ALL = "/pescados";
const POST = "/pescados/create";
const DELETE = "/pescados/delete/";
const RETRIEVE_ONE = "/pescados/retrieve/";
const UPDATE = "/pescados/update";

Vue.createApp({
    data() {
        return {
            pescados: [],
            nombrePescado: "",
            categoriaPescado: "",
            unidadPescado: "",
            tipoConservaPescado: "",
            currentPescado: {}
        };
    },
    methods: {
        async doGet() {
            try {
                const response = await axios.get(RETRIEVE_ALL);
                this.pescados = response.data;
                console.log("Pescados cargados:", response.data);
            } catch (error) {
                console.error("Error obteniendo pescados:", error);
            }
        },

        async retrievePescado(index) {
            try {
                const pescadoId = this.pescados[index].id;
                console.log("Recuperando Pescado con ID:", pescadoId);
                const response = await axios.get(RETRIEVE_ONE + pescadoId);
                this.currentPescado = response.data;
            } catch (error) {
                console.error("Error recuperando Pescado:", error);
            }
        },

        async createPescado() {
            try {
                let newPescado = {
                    nombre: this.nombre,
                    categoria: this.categoria,
                    unidad: this.unidad,
                    tipoConserva: this.tipoConserva
                };

                await axios.post(POST, newPescado);
                console.log("Pescado creado:", newPescado);
                this.doGet();

                // Limpiar los campos después de crear
                this.nombre = "";
                this.categoria = "";
                this.unidad = "";
                this.tipoConserva = "";
            } catch (error) {
                console.error("Error creando Pescado:", error);
            }
        },

        async updatePescado() {
            try {
                console.log("Actualizando Pescado:", this.currentPescado);
                await axios.put(UPDATE, this.currentPescado);
                this.doGet();
            } catch (error) {
                console.error("Error actualizando Pescado:", error);
            }
        },

        async deletePescado(index) {
            try {
                const pescadoId = this.pescados[index].id;
                console.log("Eliminando Pescado con ID:", pescadoId);
                await axios.delete(DELETE + pescadoId);
                this.doGet();
            } catch (error) {
                console.error("Error eliminando Pescado:", error);
            }
        }
    },
    mounted() {
        this.doGet();
    }
}).mount("#app");
