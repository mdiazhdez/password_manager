# Frugal passwords

Minimalistic password manager programmed in Java.

- 💻 Console based application
- 🚫 No third party libraries (besides Lombok and JUnit).

### Introduction

Welcome to **Frugal passwords**. If you need to securely store your passwords on your hard disk, but at the same time you don't trust just any software out there to manage your secrets, then this project is for you.

The project is open source, meaning you can read and understand what's going on under the hood with the secrets and how are they encrypted on a file with _.wsf_ extension (stands for _walrus secure format_).

### Master Password, Hashing and Symmetric Encryption

In order to encrypt and decrypt the secrets, users need to provide a _Master Password_, as in any Secrets manager.
This master password is provided only at runtime and shouldn't be stored on the system.

The application will use this password to generate a 32 bytes key using the secure _SHA-256_ algorithm, which subsequently will be used as the key for the symmetric encryption algorithm.

Passwords are encrypted and stored on disk using symmetric encryption, particularly _AES_.

### Important Note

Passwords are encrypted _at rest_, meaning the resulting file with all the user passwords will be securely encrypted and only someone with the correct master password will be able to decode it.

Nonetheless, as long as the application is running, passwords will be stored as _plain text_ on RAM memory as part of the application process. This is safe as long as your RAM is not compromised by hackers.

### AutoSave

After 5 minutes of inactivity the program will run an _autosave_ and exit automatically, for security measures. Meaning that any password modification (creation and/or deletions) during that session will be persisted.