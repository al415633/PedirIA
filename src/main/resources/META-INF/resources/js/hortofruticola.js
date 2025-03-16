const RETRIEVE_ALL_HORTOFRUTICOLAS = "/hortofruticolas";
const HORTOFRUTICOLAS_ADD = "/hortofruticolas";
const DELETE = "/hortofruticolas/";
const RETRIEVE_ONE_HORTOFRUTICOLA = "/hortofruticolas/";
const VALIDAR_HORTOFRUTICOLA = "/hortofruticolas/validar";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"];

Vue.createApp({
    data() {
        return {
            // Gestión de los hortofrutícolas
            hortofruticolas: [],
            nombreHortoFruticola: "",
            unidadHortoFruticola: "",
            tipoConserva: "",
            hortoFruticolaSeleccionado: {},
            imagenNombre: '',
            imagenTipo: '',
            imagenDatos: null,

            // Paginación de hortofruticolas
            currentPage: 1,
            itemsPerPage: 8, // 8 tarjetas por página (2 filas x 4 columnas)

            // Validación de datos
            nombreError: '',
            unidadError: '',
            isInvalid: false
        };
    },

    // Permite hacer visible la enumeración constante
    computed: {
        tiposConserva() {
            return TIPOS_CONSERVA;
        },
        totalPages() {
            return Math.ceil(this.hortofruticolas.length / this.itemsPerPage);
        },
        displayedHortofruticolas() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.hortofruticolas.slice(start, start + this.itemsPerPage);
        }
    },

    methods: {
        // 📌 Validación de existencia del hortofruticola en base al nombre y la unidad
        validateHortofruticola() {
            if (!this.nombreHortoFruticola || !this.unidadHortoFruticola) {
                this.nombreError = this.unidadError = '';
                this.isInvalid = false;
                return;
            }

            axios.get(`${VALIDAR_HORTOFRUTICOLA}?nombre=${this.nombreHortoFruticola}&unidad=${this.unidadHortoFruticola}`)
                .then(response => {
                    if (response.data.existe) {
                        this.nombreError = "Ya existe un hortofrutícola con este nombre y unidad.";
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

        // Obtiene el listado de hortofrutícolas completo
        async getAllHortoFruticolas() {
            try {
                const response = await axios.get(RETRIEVE_ALL_HORTOFRUTICOLAS + "/mis-hortofruticolas");
                this.carnes = response.data.map(hortofruticolaArray => {
                    const hortofruticola = hortofruticolaArray[0];
                    return {
                        ...hortofruticola,
                        imagenNombre: hortofruticolaArray[1],
                        imagenDatos: hortofruticolaArray[3],
                        imagenTipo: hortofruticolaArray[2]
                    };
                });

                this.hortofruticolas.sort((a, b) => a.nombre.localeCompare(b.nombre));
                console.log("Hortofruticolas cargados:", this.hortofruticolas);
            } catch (error) {
                console.error("Error al obtener los datos:", error);
            }
        },

        // Para cargar los datos del hortofrutícola a modificar
        async retrieveHortoFruticola(index) {
            try {
                const hortofruticolaId = this.hortofruticolas[index].id;
                const response = await axios.get(RETRIEVE_ONE_HORTOFRUTICOLA + hortofruticolaId);
                this.hortoFruticolaSeleccionado = response.data;
            } catch (error) {
                console.error("Error al recuperar el hortofruticola:", error);
            }
        },

        async createHortoFruticola() {
            if (this.isInvalid) return; // Evita el envío si hay errores

            try {
                let newHortofruticola = {
                    nombre: this.nombreHortoFruticola,
                    unidad: this.unidadHortoFruticola,
                    tipoConserva: this.tipoConserva,
                    imagenNombre: this.imagenNombre,
                    imagenTipo: this.imagenTipo,
                    imagenDatos: this.imagenDatos.split(',')[1]
                };

                const response = await axios.post(HORTOFRUTICOLAS_ADD, newHortofruticola);
                this.hortofruticolas.push(response.data);
                this.hortofruticolas.sort((a, b) => a.nombre.localeCompare(b.nombre));

                // Limpiar el formulario
                this.nombreHortoFruticola = "";
                this.unidadHortoFruticola = "";
                this.tipoConserva = "";
                this.imagenNombre = "";
                this.imagenTipo = "";
                this.imagenDatos = null;

                // Mostrar mensaje de éxito
                this.showToast('¡Hortofruticola añadido con éxito!', 'bg-success');
            } catch (error) {
                console.error("Error al crear Hortofruticola:", error);
                this.showToast('Error al crear el Hortofruticola. Inténtalo de nuevo.', 'bg-danger');
            }
        },
        //  Mostrar notificaciones tipo Toast
        showToast(message, colorClass) {
            const toastBody = document.getElementById('toast-body');
            toastBody.textContent = message;
            const toastEl = document.getElementById('toastHortofruticola');
            toastEl.classList.add(colorClass);
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        }
    },


    mounted() {
        this.getAllHortoFruticolas();
    }
}).mount("#app");
