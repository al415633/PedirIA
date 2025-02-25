const RETRIEVE_ALL = "/carnes";
const POST = "/carnes/create";
const DELETE = "/carnes/delete/";
const RETRIEVE_ONE = "/carnes/retrieve/";
const UPDATE = "/carnes/update";

Vue.createApp({
    data() {
        return {
            carnes: [],
            nombre: "",
            categoria: "",
            unidad: "",
            tipoConserva: "",
            currentCarne: {}
        };
    },
    methods: {
        async doGet() {
            try {
                const response = await axios.get(RETRIEVE_ALL);
                this.carnes = response.data;
                console.log(response.data);
            } catch (error) {
                console.error("Error getting data:", error);
            }
        },

        async retrieveCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                console.log("Retrieving Carne with ID:", carneId);
                const response = await axios.get(RETRIEVE_ONE + carneId);
                this.currentCarne = response.data;
            } catch (error) {
                console.error("Error retrieving Carne:", error);
            }
        },

        async createCarne() {
            try {
                let newCarne = {
                    nombre: this.nombre,
                    categoria: this.categoria,
                    unidad: this.unidad,
                    tipoConserva: this.tipoConserva
                };

                await axios.post(POST, newCarne);
                console.log("Carne created:", newCarne);
                this.doGet();

                // Limpiar los campos después de crear
                this.nombre = "";
                this.categoria = "";
                this.unidad = "";
                this.tipoConserva = "";
                this.id = "";
            } catch (error) {
                console.error("Error creating Carne:", error);
            }
        },

        async updateCarne() {
            try {
                console.log("Updating Carne:", this.currentCarne);
                await axios.put(UPDATE, this.currentCarne);
                this.doGet();
            } catch (error) {
                console.error("Error updating Carne:", error);
            }
        },

        async deleteCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                console.log("Deleting Carne with ID:", carneId);
                await axios.delete(DELETE + carneId);
                this.doGet();
            } catch (error) {
                console.error("Error deleting Carne:", error);
            }
        }
    },
    mounted() {
        this.doGet();
    }
}).mount("#app");
