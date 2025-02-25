const RETRIEVE_ALL = "/carne";
const POST = "/carne/create";
const DELETE = "/carne/delete/";
const RETRIEVE_ONE = "/carne/retrieve/";
const UPDATE = "/carne/update";

Vue.createApp({
    data() {
        return {
            carnes: [],
            name: "",
            type: "",
            id: "",
            currentCarne: {}
        }
    },
    methods: {
        async doGet() {
            await axios.get(RETRIEVE_ALL)
                .then((response) => {
                    this.carnes = response.data;
                    console.log(response.data);
                })
                .catch((error) => {
                    console.log("Error getting data:" + error);
                });
        },

        async retrieveCarne(index) {
            console.log("Carne at index: ", index);
            var url = RETRIEVE_ONE + this.carnes[index].id;
            var self = this;
            await axios.get(url)
                .then(function (response) {
                    console.log(response.data);
                    self.currentCarne = response.data;
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        async createCarne(name, type, id) {
            var self = this;
            let carne = {
                name: name,
                type: type,
                id: id
            };
            await axios.post(POST, carne)
                .then(function (response) {
                    console.log(response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        async updateCarne() {
            console.log("Updating");
            const self = this;
            await axios.put(UPDATE, this.currentCarne)
                .then(function (response) {
                    console.log(response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log(error);
                });
        },

        async deleteCarne(index) {
            console.log("Removing carne at index:", index);
            var self = this;
            var url = DELETE + this.carnes[index].id;
            await axios.delete(url)
                .then(function (response) {
                    console.log(response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    },
    mounted() {
        this.doGet();
    }
}).mount("#app");
