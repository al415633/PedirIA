const RETRIEVE_ALL_PESCADOS = "/pescados";
const PESCADO_ADD = "/pescados";
const DELETE = "/pescados/";
const RETRIEVE_ONE_PESCADO = "/pescados/";
const VALIDAR = "/pescados/validar";


const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "VIVO"];


Vue.createApp({
    data() {
        return {
            // Gestión de los pescados
            pescados: [],
            nombre: "",
            categoria: "",
            unidad: "",
            tipoConserva: "",
            pescadoSeleccionado: {},
            imagenNombre: '',
            imagenTipo: '',
            imagenDatos: null,

            // Paginación ¡
            currentPage: 1,
            itemsPerPage: 8,

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
            return Math.ceil(this.pescados.length / this.itemsPerPage);
        },
        displayedItems() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.pescados.slice(start, start + this.itemsPerPage);
        }
    },
    methods: {
        // Validación de existencia del producto basándonos en el nombre y la unidad, al crear
        validate() {
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

        // Obtiene el listado de pescados completo
        async getAll() {
            try {
                const response = await axios.get(RETRIEVE_ALL_PESCADOS + "/mis-pescados");
                this.pescados = response.data.map(pescadoArray => {
                    const pescado = pescadoArray[0];
                    return {
                        ...pescado,
                        imagenNombre: pescadoArray[1],
                        imagenDatos: pescadoArray[3],
                        imagenTipo: pescadoArray[2]
                    };
                });

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

        async create() {
            if (this.isInvalid) return;

            try {
                let newPescado = {
                    nombre: this.nombre,
                    categoria: this.categoria,
                    unidad: this.unidad,
                    tipoConserva: this.tipoConserva,
                    imagenNombre: this.imagenNombre,
                    imagenTipo: this.imagenTipo,
                    imagenDatos: this.imagenDatos.split(',')[1]
                };

                const response = await axios.post(PESCADO_ADD, newPescado);
                this.pescados.push(response.data);
                this.pescados.sort((a, b) => a.nombre.localeCompare(b.nombre));

                // Ordenar los pescados
                this.pescados.sort((a, b) => {
                    if (a.nombre < b.nombre) return -1;
                    if (a.nombre > b.nombre) return 1;
                    return 0;
                });

                // Limpiar el formulario
                this.nombre = "";
                this.unidad = "";
                this.tipoConserva = "";
                this.imagenNombre = "";
                this.imagenTipo = "";
                this.imagenDatos = null;

                // Mostrar el toast de éxito
                const toastBody = document.getElementById('toast-body');
                toastBody.textContent = '¡Pescado añadido con éxito!'; // Mensaje de éxito
                const toastEl = document.getElementById('toastPescado');
                const toast = new bootstrap.Toast(toastEl);
                toast.show();

            } catch (error) {
                console.error("Error al crear Pescado:", error);
                this.showToast('Error al crear el pescado. Inténtalo de nuevo.', 'bg-danger');
            }
        },

        //  Mostrar notificaciones tipo Toast
        showToast(message, colorClass) {
            const toastBody = document.getElementById('toast-body');
            toastBody.textContent = message;
            const toastEl = document.getElementById('toast');
            toastEl.classList.add(colorClass);
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        }
    },

    mounted() {
        this.getAll();
    }
}).mount("#app");
