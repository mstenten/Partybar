console.log("🔔 Admin Notification Script aktiviert");

(function() {
  if (!window.location.pathname.startsWith("/admin")) return; // Nur für Admin-Seiten

  const sound = new Audio("https://cdn.pixabay.com/audio/2022/03/15/audio_139c0e7d0f.mp3");
  let soundEnabled = localStorage.getItem('soundEnabled') !== 'false';
  let knownIds = new Set();

  // Sound-Schalter oben rechts einfügen, falls nicht vorhanden
  const toolbar = document.createElement("div");
  toolbar.innerHTML = `
    <button id="soundToggle"
            title="Sound an/aus"
            style="
              position:fixed;top:15px;right:15px;
              background:rgba(255,255,255,0.1);
              border:2px solid #ff66b2;
              border-radius:50%;
              color:#ff66b2;
              font-size:22px;
              cursor:pointer;
              width:40px;height:40px;
              display:flex;align-items:center;justify-content:center;
              z-index:9999;">${soundEnabled ? '🔊' : '🔇'}</button>
  `;
  document.body.appendChild(toolbar);

  const soundToggle = document.getElementById("soundToggle");
  soundToggle.onclick = () => {
    soundEnabled = !soundEnabled;
    localStorage.setItem('soundEnabled', soundEnabled);
    soundToggle.textContent = soundEnabled ? '🔊' : '🔇';
  };

  // Toast anzeigen
  function showToast(message) {
    const toast = document.createElement("div");
    toast.textContent = message;
    toast.className = "toast";
    Object.assign(toast.style, {
      position: "fixed",
      bottom: "20px",
      right: "20px",
      background: "rgba(255, 0, 127, 0.9)",
      color: "white",
      padding: "15px 20px",
      borderRadius: "12px",
      boxShadow: "0 0 20px rgba(255, 0, 127, 0.6)",
      animation: "fadeIn 0.5s, fadeOut 0.5s 4.5s",
      cursor: "pointer",
      zIndex: "9999"
    });
    toast.onclick = () => window.location.href = "/admin/orders";
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
  }

  // Regelmäßiger Check nach neuen Bestellungen
  setInterval(async () => {
    try {
      const res = await fetch("/admin/orders/json");
      if (!res.ok) return;
      const orders = await res.json();

      for (const order of orders) {
        const orderKey = "order-" + order.id;
        if (!knownIds.has(orderKey)) {
          knownIds.add(orderKey);

          // Sound und Popup
          if (soundEnabled) {
            sound.currentTime = 0;
            sound.play().catch(() => {});
          }

          showToast(`Neue Bestellung: ${order.drinkName} von ${order.username} 🍹`);
        }
      }
    } catch (err) {
      console.error("Fehler beim Abruf neuer Bestellungen:", err);
    }
  }, 10000);
})();
