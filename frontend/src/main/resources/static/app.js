const DEFAULT_API_URL = 'http://localhost:8080/api/jobs';
const API_URL = typeof window !== 'undefined' && window.__BACKEND_URL__
  ? window.__BACKEND_URL__
  : DEFAULT_API_URL;

const state = {
  user: {
    name: 'Carlos Mendoza',
    balance: 1240,
    balanceChange: 12,
    location: 'Ciudad de Guatemala'
  },
  jobs: [],
  notifications: [
    {
      title: 'Aplicación aceptada',
      description: 'María López aceptó tu aplicación para "Delivery en bicicleta"',
      time: 'Hace 5 min',
      icon: '✅',
      tone: 'success',
      isNew: true
    },
    {
      title: 'Nuevo mensaje',
      description: 'Roberto Sánchez: «¿Puedes empezar mañana a las 8am?»',
      time: 'Hace 1 h',
      icon: '💬',
      tone: 'info',
      isNew: true
    },
    {
      title: 'Nuevo trabajo cerca',
      description: 'Hay un nuevo trabajo de "Mensajería express" en Zona 1',
      time: 'Hace 3 h',
      icon: '📍',
      tone: 'info',
      isNew: true
    },
    {
      title: 'Nueva reseña',
      description: 'Ana Martínez te dejó una reseña de 5 estrellas',
      time: 'Hace 5 h',
      icon: '⭐️',
      tone: 'success',
      isNew: false
    }
  ],
  messages: [
    {
      name: 'María López',
      preview: '¡Perfecto! Nos vemos mañana a las 10am.',
      time: 'Hace 10 min',
      avatar: 'ML'
    },
    {
      name: 'Roberto Sánchez',
      preview: '¿Puedes empezar mañana a las 8am?',
      time: 'Hace 1 h',
      avatar: 'RS'
    },
    {
      name: 'Ana Martínez',
      preview: 'Gracias por tu excelente trabajo.',
      time: 'Hace 3 h',
      avatar: 'AM'
    },
    {
      name: 'Carlos Pérez',
      preview: 'Te envío la dirección exacta.',
      time: 'Hace 3 días',
      avatar: 'CP'
    }
  ],
  applications: [],
  search: {
    query: '',
    category: 'Todas'
  }
};

const employerProfiles = [
  { name: 'María López', zone: 'Zona 1', avatar: 'ML', rating: '4.8 ⭐️ (18)' },
  { name: 'Carlos Pérez', zone: 'Zona 4', avatar: 'CP', rating: '4.5 ⭐️ (22)' },
  { name: 'Ana Martínez', zone: 'Zona 10', avatar: 'AM', rating: '5.0 ⭐️ (15)' }
];

const scheduleOptions = [
  'Hoy, 14:00 - 18:00',
  'Mañana, 09:00 - 12:00',
  'Viernes, 16:00 - 19:00'
];

const durationOptions = ['4 horas', '3 horas', '2 horas'];

const mainScreens = [
  'login-screen',
  'register-screen',
  'home-screen',
  'search-screen',
  'publish-screen',
  'profile-screen'
];

const overlayScreens = [
  'job-detail-screen',
  'notifications-screen',
  'messages-screen',
  'applications-screen'
];

const elements = {
  homeJobs: document.getElementById('home-jobs'),
  publishList: document.getElementById('publish-list'),
  searchResults: document.getElementById('search-results'),
  searchInput: document.getElementById('search-input'),
  searchCategories: document.getElementById('search-categories'),
  formStatus: document.getElementById('form-status'),
  notificationsList: document.getElementById('notifications-list'),
  notificationsCount: document.getElementById('notifications-count'),
  notificationsTotal: document.getElementById('notifications-total'),
  messagesList: document.getElementById('messages-list'),
  messagesSearch: document.getElementById('messages-search'),
  applicationsList: document.getElementById('applications-list'),
  balanceAmount: document.getElementById('balance-amount'),
  balanceChange: document.getElementById('balance-change'),
  homeUsername: document.getElementById('home-username'),
  tabBar: document.getElementById('tab-bar'),
  screens: document.querySelector('.screens'),
  detailApply: document.getElementById('detail-apply'),
  jobDetailScreen: document.getElementById('job-detail-screen')
};

function formatCurrency(value) {
  return new Intl.NumberFormat('es-GT', {
    style: 'currency',
    currency: 'GTQ',
    maximumFractionDigits: 0
  }).format(Number(value) || 0);
}

function formatDate(dateString) {
  if (!dateString) return 'Fecha por confirmar';
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) {
    return dateString;
  }
  return date.toLocaleDateString('es-GT', {
    day: 'numeric',
    month: 'short'
  });
}

function getEmployer(jobIndex) {
  return employerProfiles[jobIndex % employerProfiles.length];
}

function hasApplication(jobId) {
  return state.applications.some((application) => String(application.job.id) === String(jobId));
}

function setDetailApplyState(jobId) {
  if (!elements.detailApply) return;
  const alreadyApplied = hasApplication(jobId);
  elements.detailApply.dataset.jobId = jobId;
  elements.detailApply.disabled = alreadyApplied;
  elements.detailApply.textContent = alreadyApplied ? 'Ya aplicaste' : 'Aplicar ahora';
  if (alreadyApplied) {
    elements.detailApply.setAttribute('aria-disabled', 'true');
  } else {
    elements.detailApply.removeAttribute('aria-disabled');
  }
}

function addNotification({ title, description, icon = 'ℹ️', tone = 'info', time = 'Hace unos instantes' }) {
  state.notifications = [
    {
      title,
      description,
      icon,
      tone,
      time,
      isNew: true
    },
    ...state.notifications
  ];
  renderNotifications();
}

function showScreen(screenId) {
  mainScreens.forEach((id) => {
    const section = document.getElementById(id);
    if (section) {
      section.classList.toggle('active', id === screenId);
    }
  });
  closeOverlays();
  updateActiveTab(screenId);
}

function updateActiveTab(screenId) {
  document.querySelectorAll('.tab-bar__button').forEach((button) => {
    const isTarget = button.dataset.target === screenId;
    button.classList.toggle('tab-bar__button--active', isTarget);
  });
}

function openOverlay(id) {
  overlayScreens.forEach((overlayId) => {
    const section = document.getElementById(overlayId);
    if (section) {
      section.classList.toggle('active', overlayId === id);
    }
  });
}

function closeOverlays() {
  overlayScreens.forEach((overlayId) => {
    const section = document.getElementById(overlayId);
    if (section) {
      section.classList.remove('active');
    }
  });
}

function setAuthenticated(isAuthenticated) {
  if (isAuthenticated) {
    elements.tabBar.classList.remove('hidden');
    showScreen('home-screen');
    renderHome();
    renderNotifications();
    renderMessages();
    renderApplications();
  } else {
    elements.tabBar.classList.add('hidden');
    showScreen('login-screen');
  }
}

function createJobCard(job, index, { compact = false, includeDelete = false } = {}) {
  const employer = getEmployer(index);
  const schedule = scheduleOptions[index % scheduleOptions.length];
  const duration = durationOptions[index % durationOptions.length];
  const amount = formatCurrency(job.payment);
  const metaDate = formatDate(job.publishedAt);

  return `
    <article class="job-card${compact ? ' job-card--compact' : ''}" data-job-id="${job.id}">
      <div class="job-card__header">
        <div>
          <span class="chip chip--light">${job.category || 'General'}</span>
          <h3 class="job-card__title">${job.title}</h3>
        </div>
        <span class="chip chip--strong">${amount}</span>
      </div>
      <div class="job-card__meta">
        <span>📍 ${job.city}</span>
        <span>🗓️ ${metaDate}</span>
        <span>🕒 ${schedule}</span>
      </div>
      <p class="job-card__body">${job.description}</p>
      <div class="job-card__footer">
        <div class="job-card__employer">
          <div class="avatar">${employer.avatar}</div>
          <div>
            <p class="employer-name">${employer.name}</p>
            <p class="employer-meta">${employer.zone} • ${employer.rating}</p>
          </div>
        </div>
        <div class="job-card__actions">
          <button type="button" class="btn btn--outline job-card__contact">Contactar</button>
          ${includeDelete ? '<button type="button" class="btn btn--danger job-card__delete">Eliminar</button>' : ''}
        </div>
      </div>
      <footer class="job-card__footer-secondary">
        <span>⏱️ Duración estimada: ${duration}</span>
      </footer>
    </article>
  `;
}

function renderHome() {
  elements.homeUsername.textContent = state.user.name;
  elements.balanceAmount.textContent = formatCurrency(state.user.balance);
  elements.balanceChange.textContent = `${state.user.balanceChange > 0 ? '+' : ''}${state.user.balanceChange}%`;

  if (!state.jobs.length) {
    elements.homeJobs.innerHTML = '<p class="empty-state">Aún no hay trabajos disponibles. Publica uno desde la pestaña Publicar.</p>';
    return;
  }

  const cards = state.jobs.slice(0, 3).map((job, index) => createJobCard(job, index)).join('');
  elements.homeJobs.innerHTML = cards;
}

function renderSearchCategories() {
  if (!elements.searchCategories) return;
  const categories = ['Todas', ...new Set(state.jobs.map((job) => job.category || 'Otros'))];
  elements.searchCategories.innerHTML = categories
    .map((category) => `
      <button type="button" class="chip ${category === state.search.category ? 'active' : ''}" data-category="${category}">
        ${category}
      </button>
    `)
    .join('');
}

function filterJobs() {
  return state.jobs.filter((job) => {
    const matchesCategory =
      state.search.category === 'Todas' || job.category === state.search.category;
    const normalizedQuery = state.search.query.trim().toLowerCase();
    const matchesQuery =
      !normalizedQuery ||
      job.title.toLowerCase().includes(normalizedQuery) ||
      job.description.toLowerCase().includes(normalizedQuery) ||
      job.city.toLowerCase().includes(normalizedQuery);
    return matchesCategory && matchesQuery;
  });
}

function renderSearchResults() {
  const jobs = filterJobs();
  if (!jobs.length) {
    elements.searchResults.innerHTML = '<p class="empty-state">No se encontraron trabajos con los filtros seleccionados.</p>';
    return;
  }
  elements.searchResults.innerHTML = jobs
    .map((job, index) => {
      const globalIndex = state.jobs.findIndex((item) => item.id === job.id);
      const referenceIndex = globalIndex >= 0 ? globalIndex : index;
      const schedule = scheduleOptions[referenceIndex % scheduleOptions.length];
      const duration = durationOptions[referenceIndex % durationOptions.length];
      return `
        <article class="search-card" data-job-id="${job.id}">
          <div class="search-card__header">
            <div>
              <span class="chip chip--light">${job.category || 'General'}</span>
              <h3 class="job-card__title">${job.title}</h3>
            </div>
            <span class="search-card__rate">${formatCurrency(job.payment)}</span>
          </div>
          <div class="job-card__meta">
            <span>📍 ${job.city}</span>
            <span>🗓️ ${formatDate(job.publishedAt)}</span>
            <span>🕒 ${schedule}</span>
          </div>
          <p class="job-card__body">${job.description}</p>
          <footer class="job-card__footer-secondary">⏱️ Duración estimada: ${duration}</footer>
          <button type="button" class="btn btn--primary job-card__apply">Aplicar</button>
        </article>
      `;
    })
    .join('');
}

function renderPublishList() {
  if (!state.jobs.length) {
    elements.publishList.innerHTML = '<p class="empty-state">Aún no tienes publicaciones activas.</p>';
    return;
  }
  elements.publishList.innerHTML = state.jobs
    .map((job, index) => createJobCard(job, index, { includeDelete: true }))
    .join('');
}

function renderNotifications() {
  const count = state.notifications.filter((notification) => notification.isNew).length;
  elements.notificationsCount.textContent = count;
  elements.notificationsTotal.textContent = count;

  elements.notificationsList.innerHTML = state.notifications
    .map((notification) => `
      <article class="list-item list-item--${notification.tone}">
        <p class="list-item__title">${notification.icon} ${notification.title}</p>
        <p class="list-item__meta">${notification.description}</p>
        <span class="list-item__time">${notification.time}</span>
      </article>
    `)
    .join('');
}

function renderMessages() {
  const query = (elements.messagesSearch?.value || '').trim().toLowerCase();
  const messages = state.messages.filter((message) =>
    !query ||
    message.name.toLowerCase().includes(query) ||
    message.preview.toLowerCase().includes(query)
  );

  elements.messagesList.innerHTML = messages
    .map((message) => `
      <article class="messages-item">
        <div class="avatar avatar--small">${message.avatar}</div>
        <div class="messages-item__content">
          <p class="messages-item__title">${message.name}</p>
          <p class="messages-item__preview">${message.preview}</p>
          <span class="messages-item__time">${message.time}</span>
        </div>
      </article>
    `)
    .join('');
}

function renderApplications() {
  if (!state.applications.length) {
    elements.applicationsList.innerHTML = '<p class="empty-state">Aún no has aplicado a trabajos.</p>';
    return;
  }

  elements.applicationsList.innerHTML = state.applications
    .map((application) => `
      <article class="list-item">
        <div class="list-item__header">
          <h3 class="list-item__title">${application.job.title}</h3>
          <span class="chip chip--${application.status.tone}">${application.status.label}</span>
        </div>
        <p class="list-item__meta">${application.employer.name} • ${application.employer.zone}</p>
        <p class="list-item__meta">${application.status.meta}</p>
        <span class="list-item__time">${application.appliedAgo}</span>
      </article>
    `)
    .join('');
}

function updateApplicationsFromJobs() {
  const statuses = [
    { label: 'Aceptada', tone: 'success', meta: 'Inicio: Mañana 14:00' },
    { label: 'Pendiente', tone: 'warning', meta: 'Esperando respuesta del empleador' }
  ];

  const manualApplications = state.applications
    .filter((application) => application.source === 'manual')
    .map((application) => {
      const job = state.jobs.find((item) => String(item.id) === String(application.job.id));
      if (!job) {
        return null;
      }
      return {
        ...application,
        job
      };
    })
    .filter(Boolean);

  const manualIds = new Set(manualApplications.map((application) => String(application.job.id)));

  const generatedApplications = state.jobs
    .slice(0, 2)
    .map((job, index) => ({ job, index }))
    .filter(({ job }) => !manualIds.has(String(job.id)))
    .map(({ job, index }) => ({
      source: 'generated',
      job,
      employer: getEmployer(index),
      status: statuses[index % statuses.length],
      appliedAgo: index === 0 ? 'Aplicado hace 2 días' : 'Aplicado hace 1 día'
    }));

  state.applications = [...manualApplications, ...generatedApplications];
}

function applyToJob(jobId) {
  const jobIndex = state.jobs.findIndex((job) => String(job.id) === String(jobId));
  if (jobIndex === -1) {
    return;
  }

  if (hasApplication(jobId)) {
    setDetailApplyState(jobId);
    renderApplications();
    openOverlay('applications-screen');
    return;
  }

  const job = state.jobs[jobIndex];
  const manualApplication = {
    source: 'manual',
    job,
    employer: getEmployer(jobIndex),
    status: {
      label: 'En revisión',
      tone: 'info',
      meta: 'El empleador revisará tu solicitud'
    },
    appliedAgo: 'Hace unos instantes'
  };

  state.applications = [manualApplication, ...state.applications];
  renderApplications();
  setDetailApplyState(jobId);
  addNotification({
    title: 'Solicitud enviada',
    description: `Aplicaste a «${job.title}»`,
    icon: '📝',
    tone: 'success'
  });
  openOverlay('applications-screen');
}

function openJobDetail(jobId) {
  const jobIndex = state.jobs.findIndex((job) => String(job.id) === String(jobId));
  if (jobIndex === -1) return;
  const job = state.jobs[jobIndex];
  const employer = getEmployer(jobIndex);

  document.getElementById('detail-category').textContent = job.category || 'General';
  document.getElementById('detail-title').textContent = job.title;
  document.getElementById('detail-rate').textContent = formatCurrency(job.payment);
  document.getElementById('detail-location').textContent = `${job.city}`;
  document.getElementById('detail-schedule').textContent = scheduleOptions[jobIndex % scheduleOptions.length];
  document.getElementById('detail-duration').textContent = durationOptions[jobIndex % durationOptions.length];
  document.getElementById('detail-employer').textContent = employer.name;
  document.getElementById('detail-description').textContent = job.description;
  document.getElementById('detail-phone').textContent = job.contactPhone || 'No indicado';
  document.getElementById('detail-email').textContent = job.contactEmail || 'No indicado';

  const avatar = document.querySelector('#job-detail-screen .avatar');
  if (avatar) {
    avatar.textContent = employer.avatar;
  }

  if (elements.jobDetailScreen) {
    elements.jobDetailScreen.dataset.jobId = job.id;
  }

  setDetailApplyState(job.id);

  openOverlay('job-detail-screen');
}

async function loadJobs() {
  elements.homeJobs.innerHTML = '<p class="loading">Cargando trabajos...</p>';
  elements.searchResults.innerHTML = '<p class="loading">Buscando trabajos...</p>';
  elements.publishList.innerHTML = '<p class="loading">Actualizando publicaciones...</p>';

  try {
    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error('No fue posible obtener los trabajos.');
    }
    const jobs = await response.json();
    state.jobs = jobs
      .slice()
      .sort((a, b) => new Date(b.publishedAt) - new Date(a.publishedAt));
    updateApplicationsFromJobs();
    renderHome();
    renderSearchCategories();
    renderSearchResults();
    renderPublishList();
    renderApplications();
    const currentDetailId = elements.jobDetailScreen?.dataset?.jobId;
    if (currentDetailId) {
      setDetailApplyState(currentDetailId);
    }
  } catch (error) {
    const message = `<p class="empty-state">${error.message}</p>`;
    elements.homeJobs.innerHTML = message;
    elements.searchResults.innerHTML = message;
    elements.publishList.innerHTML = message;
    console.error(error);
  }
}

async function deleteJob(jobId) {
  if (!confirm('¿Eliminar este trabajo?')) {
    return;
  }
  try {
    const response = await fetch(`${API_URL}/${jobId}`, { method: 'DELETE' });
    if (!response.ok && response.status !== 204) {
      throw new Error('No se pudo eliminar el trabajo');
    }
    state.jobs = state.jobs.filter((job) => String(job.id) !== String(jobId));
    updateApplicationsFromJobs();
    renderHome();
    renderSearchResults();
    renderPublishList();
    renderApplications();
  } catch (error) {
    alert(error.message);
  }
}

function setupEventListeners() {
  const loginForm = document.getElementById('login-form');
  loginForm?.addEventListener('submit', (event) => {
    event.preventDefault();
    setAuthenticated(true);
  });

  document.getElementById('open-register')?.addEventListener('click', () => {
    showScreen('register-screen');
  });

  document.getElementById('back-to-login')?.addEventListener('click', () => {
    showScreen('login-screen');
  });

  document.getElementById('register-form')?.addEventListener('submit', (event) => {
    event.preventDefault();
    setAuthenticated(true);
  });

  document.querySelectorAll('.tab-bar__button').forEach((button) => {
    button.addEventListener('click', () => {
      const target = button.dataset.target;
      if (target) {
        showScreen(target);
      }
    });
  });

  document.querySelectorAll('[data-target]').forEach((button) => {
    button.addEventListener('click', () => {
      const target = button.dataset.target;
      if (target) {
        showScreen(target);
      }
    });
  });

  document.querySelectorAll('[data-overlay]').forEach((button) => {
    button.addEventListener('click', () => {
      const overlayId = button.dataset.overlay;
      if (overlayId) {
        if (overlayId === 'notifications') {
          renderNotifications();
        }
        if (overlayId === 'messages') {
          renderMessages();
        }
        if (overlayId === 'applications') {
          renderApplications();
        }
        openOverlay(`${overlayId}-screen`);
      }
    });
  });

  document.querySelectorAll('.overlay-close').forEach((button) => {
    button.addEventListener('click', () => closeOverlays());
  });

  elements.searchCategories?.addEventListener('click', (event) => {
    const { target } = event;
    if (target instanceof HTMLElement && target.dataset.category) {
      state.search.category = target.dataset.category;
      renderSearchCategories();
      renderSearchResults();
    }
  });

  elements.searchInput?.addEventListener('input', (event) => {
    state.search.query = event.target.value;
    renderSearchResults();
  });

  elements.messagesSearch?.addEventListener('input', () => {
    renderMessages();
  });

  document.getElementById('mark-notifications')?.addEventListener('click', () => {
    state.notifications = state.notifications.map((notification) => ({
      ...notification,
      isNew: false
    }));
    renderNotifications();
  });

  elements.screens?.addEventListener('click', (event) => {
    const card = event.target.closest('[data-job-id]');
    if (!card || card.closest('.overlay')) {
      return;
    }
    const jobId = card.dataset.jobId;
    const deleteButton = event.target.closest('.job-card__delete');
    if (deleteButton) {
      event.preventDefault();
      event.stopPropagation();
      deleteJob(jobId);
      return;
    }
    const contactButton = event.target.closest('.job-card__contact');
    if (contactButton) {
      event.preventDefault();
      event.stopPropagation();
      renderMessages();
      openOverlay('messages-screen');
      return;
    }
    const applyButton = event.target.closest('.job-card__apply');
    if (applyButton) {
      event.preventDefault();
      event.stopPropagation();
      applyToJob(jobId);
      return;
    }
    openJobDetail(jobId);
  });

  const jobForm = document.getElementById('job-form');
  jobForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(jobForm);
    const payload = Object.fromEntries(formData.entries());
    payload.payment = Number(payload.payment);
    payload.publishedAt = new Date().toISOString().split('T')[0];

    elements.formStatus.textContent = 'Publicando...';
    elements.formStatus.classList.remove('error');

    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}));
        throw new Error(errorBody.error || 'No fue posible crear el trabajo');
      }
      jobForm.reset();
      elements.formStatus.textContent = 'Trabajo publicado correctamente';
      await loadJobs();
    } catch (error) {
      elements.formStatus.textContent = error.message;
      elements.formStatus.classList.add('error');
    }
  });

  elements.detailApply?.addEventListener('click', () => {
    const { jobId } = elements.jobDetailScreen?.dataset || {};
    const fallbackId = elements.detailApply?.dataset.jobId;
    const targetJobId = jobId || fallbackId;
    if (targetJobId) {
      applyToJob(targetJobId);
    }
  });
}

setAuthenticated(false);
setupEventListeners();
renderNotifications();
renderMessages();
renderApplications();
loadJobs();
