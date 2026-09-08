# 🚀 Guide Rapide : Installer et Lancer le Projet sur une Nouvelle Machine

Suivez ces **6 étapes simples** pour faire fonctionner l'application **Note Mail** sur un nouvel ordinateur.

---

## 📋 Prérequis à Installer

Avant de commencer, vérifiez que ces 3 logiciels sont installés sur le nouvel ordinateur :

1. ☕ **Java JDK 17 ou 21** : [Télécharger Adoptium Temurin](https://adoptium.net/)
   - *Vérification dans le terminal :* `java -version`
2. 💻 **VS Code** : [Télécharger VS Code](https://code.visualstudio.com/)
   - Installez l'extension **Extension Pack for Java** (de Microsoft) depuis l'onglet Extensions (`Ctrl + Maj + X`).
3. 🐙 **Git** : [Télécharger Git](https://git-scm.com/)

---

## 📥 Étape 1 : Récupérer le Projet

Ouvrez un terminal (CMD ou PowerShell) et exécutez :

```bash
git clone https://github.com/VOTRE_PSEUDO/note-mail.git
cd note-mail
```

*(Ou dézippez le dossier du projet si vous l'avez transféré par clé USB.)*

---

## 📊 Étape 2 : Préparer le Fichier Excel

1. Créez un dossier nommé `data` à la racine du projet s'il n'existe pas.
2. Placez-y votre fichier `donnees.xlsx` :

```
📁 note-mail/
├── 📁 data/
│   └── 📊 donnees.xlsx   <-- DOIT ÊTRE EXACTEMENT ICI
├── 📁 src/
└── 📄 pom.xml
```

> ⚠️ **ATTENTION** : Fermez complètement Microsoft Excel sur l'ordinateur ! Si le fichier `.xlsx` est ouvert dans Excel, l'application ne pourra pas le lire.

---

## 🔑 Étape 3 : Générer un Mot de Passe d'Application Gmail

Si vous utilisez un nouveau compte Gmail expéditeur sur cette machine :

1. Connectez-vous à votre compte Gmail.
2. Allez dans **Gérer votre compte Google > Sécurité**.
3. Activez la **Validation en deux étapes**.
4. Cherchez **"Mots de passe des applications"**.
5. Générez un nouveau mot de passe nommé `NoteMail` et copiez la clé de 16 lettres (ex : `abcd efgh ijkl mnop`).

---

## ⚙️ Étape 4 : Configurer `application.properties`

Ouvrez le fichier `src/main/resources/application.properties` dans VS Code et renseignez vos identifiants :

```properties
server.port=8081

# CONFIGURATION GMAIL SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre.email@gmail.com
spring.mail.password=votre_code_application_16_lettres
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
spring.mail.properties.mail.smtp.ssl.protocols=TLSv1.2
```

---

## ▶️ Étape 5 : Lancer l'Application dans VS Code

1. Ouvrez VS Code.
2. Cliquez sur **Fichier > Ouvrir le dossier...** et choisissez le dossier `note-mail`.
3. Attendez 10 à 30 secondes que Maven synchronise les dépendances (Apache POI, OpenPDF, Spring Mail).
4. Ouvrez le fichier :
   `src/main/java/com/note_mail/NoteMailApplication.java`
5. Cliquez sur le bouton **Run** (situé au-dessus de `public static void main`).

---

## 🧪 Étape 6 : Tester dans le Navigateur

1. Ouvrez votre navigateur et allez sur : `http://localhost:8081`
2. Cliquez sur **"ENVOYER UN RELEVÉ DE NOTE"**.
3. Sélectionnez un matricule dans la liste déroulante (l'email se remplit automatiquement).
4. Cliquez sur **"Envoyer →"**.
5. Vérifiez la réception du mail avec le PDF en pièce jointe !
