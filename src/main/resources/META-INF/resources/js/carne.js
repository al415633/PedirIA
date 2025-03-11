const RETRIEVE_ALL_CARNES = "/carnes";
const CARNE_ADD = "/carnes";
const DELETE = "/carnes/";
const RETRIEVE_ONE_CARNE = "/carnes/";
const VALIDAR_CARNE = "/carnes/validar";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"];

Vue.createApp({
    data() {
        return {
            // Gestión de las carnes
            carnes: [],
            nombreCarne: "",
            unidadCarne: "",
            tipoConserva: "",
            carneSeleccionada: {},
            imagenNombre: '',
            imagenTipo: '',
            imagenDatos: null,

            // Paginación de carnes
            currentPage: 1,
            itemsPerPage: 8, // 8 tarjetas por página (2 filas x 4 columnas)

            // Validación de datos
            nombreError: '',
            unidadError: '',
            isInvalid: false
        };
    },

    computed: {
        tiposConserva() {
            return TIPOS_CONSERVA;
        },
        totalPages() {
            return Math.ceil(this.carnes.length / this.itemsPerPage);
        },
        displayedCarnes() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.carnes.slice(start, start + this.itemsPerPage);
        }
    },

    methods: {
        // Validación de existencia de la carne basándonos en el nombre y la unidad
        validateCarne() {
            if (!this.nombreCarne || !this.unidadCarne) {
                this.nombreError = this.unidadError = '';
                this.isInvalid = false;
                return;
            }

            axios.get(`${VALIDAR_CARNE}?nombre=${this.nombreCarne}&unidad=${this.unidadCarne}`)
                .then(response => {
                    if (response.data.existe) {
                        this.nombreError = "Ya existe una carne con este nombre y unidad.";
                        this.unidadError = "Por favor, elija un nombre o unidad diferente.";
                        this.isInvalid = true;
                    } else {
                        this.nombreError = this.unidadError = '';
                        this.isInvalid = false;
                    }
                })
                .catch(error => {
                    console.error("Error en la validación:", error);
                    this.nombreError = "Error al validar el nombre.";
                    this.isInvalid = true;
                });
        },

        // Captura de imagen
        onFileChange(event) {
            const file = event.target.files[0];
            if (!file) return;

            this.imagenNombre = file.name;
            this.imagenTipo = file.type;

            const reader = new FileReader();
            reader.onload = (e) => {
                this.imagenDatos = e.target.result;
            };
            reader.readAsDataURL(file);
        },

        // Métodos para paginación
        prevPage() {
            if (this.currentPage > 1) this.currentPage--;
        },
        nextPage() {
            if (this.currentPage < this.totalPages) this.currentPage++;
        },
        goToPage(page) {
            this.currentPage = page;
        },

        // Obtiene el listado de carnes completo
        async getAllCarnes() {
            try {
                const response = await axios.get(RETRIEVE_ALL_CARNES + "/mis-carnes");
                this.carnes = response.data.map(carneArray => {
                    const carne = carneArray[0];
                    return {
                        ...carne,
                        imagenNombre: carneArray[1],
                        imagenDatos: carneArray[3],
                        imagenTipo: carneArray[2]
                    };
                });

                this.carnes.sort((a, b) => a.nombre.localeCompare(b.nombre));
                console.log("Carnes cargadas:", this.carnes);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

        // Recuperar una carne específica
        async retrieveCarne(index) {
            try {
                const carneId = this.carnes[index].id;
                const response = await axios.get(RETRIEVE_ONE_CARNE + carneId);
                this.carneSeleccionada = response.data;
            } catch (error) {
                console.error("Error al recuperar la Carne:", error);
            }
        },

        //  Crear una nueva carne
        async createCarne() {
            if (this.isInvalid) return; // Evita el envío si hay errores

            try {
                let newCarne = {
                    nombre: this.nombreCarne,
                    unidad: this.unidadCarne,
                    tipoConserva: this.tipoConserva,
                    imagenNombre: this.imagenNombre,
                    imagenTipo: this.imagenTipo,
                    imagenDatos: this.imagenDatos.split(',')[1]
                };

                const response = await axios.post(CARNE_ADD, newCarne);
                this.carnes.push(response.data);
                this.carnes.sort((a, b) => a.nombre.localeCompare(b.nombre));

                // Limpiar el formulario
                this.nombreCarne = "";
                this.unidadCarne = "";
                this.tipoConserva = "";
                this.imagenNombre = "";
                this.imagenTipo = "";
                this.imagenDatos = null;

                // Mostrar mensaje de éxito
                this.showToast('¡Carne añadida con éxito!', 'bg-success');
            } catch (error) {
                console.error("Error al crear Carne:", error);
                this.showToast('Error al crear la carne. Inténtalo de nuevo.', 'bg-danger');
            }
        },

        //  Mostrar notificaciones tipo Toast
        showToast(message, colorClass) {
            const toastBody = document.getElementById('toast-body');
            toastBody.textContent = message;
            const toastEl = document.getElementById('toastCarne');
            toastEl.classList.add(colorClass);
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        }
    },

    mounted() {
        this.getAllCarnes();
    }
}).mount("#app");
