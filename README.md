# 📧 Note Mail - Envoi Automatique de Relevés de Notes

Application Web développée avec **Spring Boot** permettant de lire les données des étudiants depuis un fichier Excel, de générer dynamiquement un relevé de notes au format PDF et de l'envoyer par Email via **JavaMail (Gmail SMTP)**.

## 🎨 Aperçu
- Design sombre moderne avec thème **Vert Néon**
- Recherche dynamique d'étudiants en temps réel
- Interface avec fenêtres modales
- Fichier PDF conforme aux normes académiques (ENI / Université de Fianarantsoa)

## 🛠️ Technologies utilisées
- **Backend :** Java 17+, Spring Boot 3, Spring MVC
- **Frontend :** HTML5, CSS3, JavaScript (Vanilla), Thymeleaf
- **Data :** Apache POI (Lecture de fichiers `.xlsx`)
- **PDF :** OpenPDF (iText)
- **Email :** Jakarta Mail / Spring Boot Starter Mail (SMTP Gmail)

## 🚀 Installation & Exécution
1. Cloner le projet : `git clone https://github.com/VOTRE_PSEUDO/note-mail.git`
2. Placer le fichier Excel `donnees.xlsx` dans le dossier `/data`
3. Configurer vos identifiants Gmail dans `src/main/resources/application.properties`
4. Lancer l'application : `mvn spring-boot:run`
5. Ouvrir le navigateur sur `http://localhost:8081`
