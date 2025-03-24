const API_PESCADOS = "/pescados";
const GET_PESCADOS_PROPIOS = "/pescados/mis-productos";
const VALIDAR_PESCADO = "/pescados/validar";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "VIVO"];


Vue.createApp({
    data() {
        return {
            // Gestión de los pescados
            pescados: [],
            nombrePescado: "",
            unidadPescado: "",
            tipoConserva: "",
            pescadoSeleccionado: {},
            imagenNombre: '',
            imagenTipo: '',
            imagenDatos: null,

            // Paginación ¡
            currentPage: 1,
            itemsPerPage: 8,

            // Errores
            errores: { nombre: '', unidad: '', general: '' }
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
        displayedPescados() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.pescados.slice(start, start + this.itemsPerPage);
        },
        botonDeshabilitado() {
            return this.errores.nombre.length > 0 || this.errores.unidad.length > 0;
        }
    },
    methods: {
        // Validación de existencia del producto basándonos en el nombre y la unidad, al crear
        async validatePescado() {
            if (!this.nombrePescado || !this.unidadPescado) {
                this.errores.nombre = this.errores.unidad = '';
                return;
            }
            try {
                const { data } = await axios.get(`${VALIDAR_PESCADO}?nombre=${this.nombrePescado}&unidad=${this.unidadPescado}`);
                if (data.existe) {
                    this.errores.nombre = "Ya existe un pescado con este nombre.";
                    this.errores.unidad = "Por favor, elija otro nombre o unidad.";
                } else {
                    this.errores.nombre = this.errores.unidad = '';
                }
            } catch (error) {
                console.error("Error en la validación:", error);
                this.errores.nombre = "Error al validar.";
            }
        },

        // Manejo de archivo de imagen
        onFileChange(event) {
            const file = event.target.files[0];
            if (!file) return;

            this.imagenNombre = file.name;
            this.imagenTipo = file.type;

            const reader = new FileReader();
            reader.onload = (e) => {
                this.imagenDatos = e.target.result.split(',')[1]; // Separa el encabezado de Base64
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

        async getAllPescados() {
            try {
                const { data } = await axios.get(`${GET_PESCADOS_PROPIOS}`);
                this.pescados = data.map(pescadoArray => {
                    const pescado = pescadoArray[0]; // Extrae el objeto pescado del array interno
                    return {
                        ...pescado,
                        imagenNombre: pescadoArray[1] || "",
                        imagenTipo: pescadoArray[2] || "",
                        imagenDatos: pescadoArray[3] || null
                    };
                });
                console.log(this.pescados)
                this.pescados.sort((a, b) => a.nombre.localeCompare(b.nombre));
            } catch (error) {
                console.error("Error al obtener pescados:", error);
            }
        },

        // Recuperar una pescado específico
        async retrievePescado(id) {
            try {
                const { data } = await axios.get(`${API_PESCADOS}/${id}`);
                this.pescadoSeleccionado = data;
            } catch (error) {
                console.error("Error al recuperar el pescado:", error);
            }
        },

        // Crear un nuevo pescado
        async createPescado() {
            if (this.errores.nombre || this.errores.unidad) return; // Evita si hay errores

            try {
                const newPescado = {
                    nombre: this.nombrePescado,
                    unidad: this.unidadPescado,
                    tipoConserva: this.tipoConserva,
                    imagenNombre: this.imagenNombre,
                    imagenTipo: this.imagenTipo,
                    imagenDatos: this.imagenDatos
                };

                const { data } = await axios.post(API_PESCADOS, newPescado);
                this.pescados.push(data);
                this.pescados.sort((a, b) => a.nombre.localeCompare(b.nombre));

                // Limpiar formulario
                this.resetForm();
                this.showToast('¡Pescado añadido con éxito!', 'bg-success');
            } catch (error) {
                console.error("Error al crear Pescado:", error);
                this.showToast('Error al crear el pescado.', 'bg-danger');
            }
        },


        // Reinicia los campos del formulario
        resetForm() {
            this.nombrePescado = "";
            this.unidadPescado = "";
            this.tipoConserva = "";
            this.imagenNombre = "";
            this.imagenTipo = "";
            this.imagenDatos = null;
            this.errores = { nombre: '', unidad: '', general: '' };
        },

        // Mostrar notificaciones tipo Toast
        showToast(message, colorClass) {
            const toastBody = document.getElementById('toast-body');
            toastBody.textContent = message;
            const toastEl = document.getElementById('toastPescado');
            toastEl.classList.add(colorClass);
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        }
    },
    mounted() {
        this.getAllPescados();
    }
}).mount("#app");
