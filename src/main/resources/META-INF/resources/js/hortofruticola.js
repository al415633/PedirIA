const API_HORTOFRUTICOLAS = "/hortofruticolas";
const GET_HORTOFRUTICOLAS_PROPIAS = "/hortofruticolas/mis-productos";
const VALIDAR_HORTOFRUTICOLA = "/hortofruticolas/validar";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "SECO"];

Vue.createApp({
    data() {
        return {
            // Gestión de los hortofrutícolas
            hortofruticolas: [],
            nombreHortofruticola: "",
            unidadHortofruticola: "",
            tipoConserva: "",
            hortoFruticolaSeleccionado: {},
            imagenNombre: '',
            imagenTipo: '',
            imagenDatos: null,

            // Paginación de hortofruticolas
            currentPage: 1,
            itemsPerPage: 8, // 8 tarjetas por página (2 filas x 4 columnas)

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
            return Math.ceil(this.hortofruticolas.length / this.itemsPerPage);
        },
        displayedHortofruticolas() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.hortofruticolas.slice(start, start + this.itemsPerPage);
        },
        botonDeshabilitado() {
            return this.errores.nombre.length > 0 || this.errores.unidad.length > 0;
        }
    },

    methods: {
        async validateHortofruticola() {
            if (!this.nombreHortofruticola || !this.unidadHortofruticola) {
                this.errores.nombre = this.errores.unidad = '';
                return;
            }
            try {
                const { data } = await axios.get(`${VALIDAR_HORTOFRUTICOLA}?nombre=${this.nombreHortofruticola}&unidad=${this.unidadHortofruticola}`);
                if (data.existe) {
                    this.errores.nombre = "Ya existe una hortofruticola con este nombre.";
                    this.errores.unidad = "Por favor, elija otro nombre o unidad.";
                } else {
                    this.errores.nombre = this.errores.unidad = '';
                }
            } catch (error) {
                console.error("Error en la validación:", error);
                this.errores.nombre = "Error al validar.";
            }
        },

        // Captura de imagen
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

        // Obtiene la lista de hortofruticolas del usuario
        async getAllHortofruticolas() {
            try {
                const { data } = await axios.get(`${GET_HORTOFRUTICOLAS_PROPIAS}`);
                this.hortofruticolas = data.map(hortofruticolaArray => {
                    const hortofruticola = hortofruticolaArray[0]; // Extrae el objeto Hortofrutícola del array interno
                    return {
                        ...hortofruticola,
                        imagenNombre: hortofruticolaArray[1] || "",
                        imagenTipo: hortofruticolaArray[2] || "",
                        imagenDatos: hortofruticolaArray[3] || null
                    };
                });
                console.log(this.hortofruticolas)
                this.hortofruticolas.sort((a, b) => a.nombre.localeCompare(b.nombre));
            } catch (error) {
                console.error("Error al obtener hortofruticolas:", error);
            }
        },

        // Recuperar una hortofruticola específica
        async retrieveHortofruticola(id) {
            try {
                const { data } = await axios.get(`${API_HORTOFRUTICOLAS}/${id}`);
                this.hortoFruticolaSeleccionado = data;
            } catch (error) {
                console.error("Error al recuperar la hortofruticola:", error);
            }
        },

        // Crear una nueva hortofruticola
        async createHortofruticola() {
            if (this.errores.nombre || this.errores.unidad) return; // Evita si hay errores

            try {
                const newHortofruticola = {
                    nombre: this.nombreHortofruticola,
                    unidad: this.unidadHortofruticola,
                    tipoConserva: this.tipoConserva,
                    imagenNombre: this.imagenNombre,
                    imagenTipo: this.imagenTipo,
                    imagenDatos: this.imagenDatos
                };

                const { data } = await axios.post(API_HORTOFRUTICOLAS, newHortofruticola);
                this.hortofruticolas.push(data);
                this.hortofruticolas.sort((a, b) => a.nombre.localeCompare(b.nombre));

                // Limpiar formulario
                this.resetForm();
                this.showToast('¡Hortofrutícola añadida con éxito!', 'bg-success');
            } catch (error) {
                console.error("Error al crear hortofrutícola:", error);
                this.showToast('Error al crear la hortofrutícola.', 'bg-danger');
            }
        },

        // Reinicia los campos del formulario
        resetForm() {
            this.nombreHortofruticola = "";
            this.unidadHortofruticola = "";
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
            const toastEl = document.getElementById('toastHortofruticola');
            toastEl.classList.add(colorClass);
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        }
    },


    mounted() {
        this.getAllHortofruticolas();
    }
}).mount("#app");
