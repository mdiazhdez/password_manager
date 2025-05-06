package com.morsaprogramando.secret_manager.view;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.morsaprogramando.secret_manager.models.StoredPassword;
import com.morsaprogramando.secret_manager.services.FileManagerService;
import com.morsaprogramando.secret_manager.services.PasswordManagerService;

public class KeystoreMenu {

	private final long maxInactivityTimeMs = 300_000;

	private final PasswordManagerService passwordManagerService;
	private final List<StoredPassword> passwords;
	private final FileManagerService fileManagerService;
	private State currentState = State.CHOOSE;
	private boolean unsavedChanges = false;
	private long lastActivityInMs = System.currentTimeMillis();
	private final AtomicBoolean saveLock = new AtomicBoolean(false);

	public KeystoreMenu(PasswordManagerService passwordManagerService, List<StoredPassword> passwords,
			FileManagerService fileManagerService) {
		this.passwordManagerService = passwordManagerService;
		this.passwords = passwords;
		this.fileManagerService = fileManagerService;
	}

	public void render() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		try {
			Future<?> future = executorService.submit(this::monitorIdleTimeInBackground);
			renderInternal(future);
		} finally {
			executorService.shutdown();
			try {
				if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
					executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				executorService.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}

	private void renderInternal(Future<?> future) {
        while (true) {
            lastActivityInMs = System.currentTimeMillis();

            Utils.clearScreen();
            Utils.println("");

            if (currentState == State.EXIT) {
                if (saveLock.compareAndSet(false, true)) {
                    future.cancel(true);
                    return;
                }

                // retry to obtain lock and exit
                continue;
            }

            printPasswords();

            switch (currentState) {
                case CHOOSE: {printChooseMenu(); break;}
                case CREATE_PASS: {printCreatePassMenu(); break;}
                case READ_PASS: {printReadPassMenu(); break;}
                case DEL_PASS: {printDeletePassMenu(); break;}
                case SAVE: {printSaveMenu(); break;}
			default:
				break;
            }

        }
    }

	private void printSaveMenu() {
		try {
			if (!unsavedChanges) {
				Utils.println("No changes to be saved");
				Utils.readLine("Press Enter to continue...");

				this.currentState = State.CHOOSE;
				return;
			}

			concurrentSave();

			Utils.println("");
			Utils.println("Passwords saved successfully!");
			Utils.readLine("Press Enter to continue...");

			this.currentState = State.CHOOSE;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void printDeletePassMenu() {
		String selectedTitle;
		StoredPassword selectedPassword = null;

		outer: while (true) {
			try {
				selectedTitle = Utils.readLine("Title to be deleted: ");

				for (StoredPassword password : passwords) {
					if (Objects.equals(password.getTitle(), selectedTitle)) {
						selectedPassword = password;
						break outer;
					}
				}

				Utils.println("Title not found.");
				break;

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			Utils.println("");
			if (selectedPassword != null) {
				passwords.remove(selectedPassword);
				unsavedChanges = true;

				Utils.readLine("Password \"" + selectedPassword.getTitle() + "\" was removed. Press Enter to continue...");
			} else {
				Utils.readLine("No password was removed. Press Enter to continue...");
			}

			this.currentState = State.CHOOSE;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void printReadPassMenu() {
		int selectedPassword;

		while (true) {
			try {
				selectedPassword = Utils.readInt("Enter the password ID: ");

				if (selectedPassword < 1 || selectedPassword > passwords.size()) {
					Utils.println("ID not found, try again.");
					continue;
				}
				break;

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			Utils.println("");
			Utils.println(getPasswords().get(selectedPassword - 1).getPassword());
			Utils.readLine("Press enter to hide the password...");

			this.currentState = State.CHOOSE;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void printPasswords() {
		if (passwords.isEmpty()) {
			Utils.println("No passwords yet, create your first password.");
			printDelimiter('=', 130);
			Utils.println("");
			return;
		}

		System.out.printf("%-5s %-30s %-50s %-20s %-10s%n", "Id","Title", "Username", "Password", "Created At");

		printDelimiter('-', 130);

		int id = 0;
		for (StoredPassword password : getPasswords()) {
			System.out.printf("%-5s %-30s %-50s %-20s %-10s%n", ++id, password.getTitle(), password.getUsername(), "******",
					password.createdAtAsString());
		}

		printDelimiter('=', 130);
		Utils.println("");
	}

	private void printCreatePassMenu() {
		try {
			
			String title = "";
			boolean alreadyExists = true;

			outer: do {
				if (!title.isEmpty()) {
					Utils.println("The title already exists, try another one.");
				}

				title = Utils.readLine("Title: ");

				for (StoredPassword password : passwords) {
					if (Objects.equals(password.getTitle(), title)) {
						continue outer;
					}
				}

				alreadyExists = false;

			} while (alreadyExists);

			String username = Utils.readLine("Username: ");
			String password = Utils.readLine("Password: ");

			StoredPassword storedPassword = new StoredPassword(title, username, password, Instant.now());
			this.passwords.add(storedPassword);

			unsavedChanges = true;

			this.currentState = State.CHOOSE;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void printChooseMenu() {
		Utils.println("1. Add a new password");
		Utils.println("2. View existing password");
		Utils.println("3. Delete a password");
		Utils.println("4. Save changes");
		Utils.println("5. Quit");

		try {
			String optionRaw = Utils.readLine("Select an option: ");
			int option;

			try {
				option = Integer.parseInt(optionRaw);
			} catch (NumberFormatException e) {
				Utils.readLine("Not a valid option! Press (Enter) to return...");
				return;
			}

			if (option == State.READ_PASS.option && passwords.isEmpty()) {
				Utils.println("\nNo password available to read!");
				Utils.readLine("Press (Enter) to continue...");
				this.currentState = State.CHOOSE;
				return;
			}

			if (option == State.DEL_PASS.option && passwords.isEmpty()) {
				Utils.println("\nNo password available to delete!");
				Utils.readLine("Press (Enter) to continue...");
				this.currentState = State.CHOOSE;
				return;
			}

			if (option == State.EXIT.option && unsavedChanges) {
				String answer = Utils
						.readLine("There are unsaved changes!\nAre you sure you want to quit? (Y)es (N)o: ");
				if (!"y".equalsIgnoreCase(answer)) {
					this.currentState = State.CHOOSE;
					return;
				}
			}

			this.currentState = State.fromNumber(option);

		} catch (Exception e) {
			Utils.println("An internal problem occurred. Exiting...");
			System.exit(1);
		}
	}

	public static void printDelimiter(char repeatChar, int length) {
		StringBuilder delimiter = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			delimiter.append(repeatChar);
		}
		Utils.println(delimiter.toString());		    
	}

	private List<StoredPassword> getPasswords() {
		return passwords.stream().sorted().collect(Collectors.toList());
	}

	private void monitorIdleTimeInBackground() {
		try {
			while (true) {
				Thread.sleep(10_000);

				long now = System.currentTimeMillis();

				if (now - lastActivityInMs >= maxInactivityTimeMs
						&& (currentState == State.CHOOSE || currentState == State.READ_PASS)) {
					if (unsavedChanges && !concurrentSave()) {
						continue; // try again to save
					}

					System.exit(0);
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean concurrentSave() {
		if (saveLock.compareAndSet(false, true)) {
			save();
			saveLock.set(false);
			return true;
		}

		return false;
	}

	private void save() {
		try {
			byte[] encryptedPasswords = passwordManagerService.encodePasswords(passwords);
			fileManagerService.write(encryptedPasswords);

			unsavedChanges = false;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private enum State {
		CHOOSE(0), CREATE_PASS(1), READ_PASS(2), DEL_PASS(3), SAVE(4), EXIT(5);
		final int option;

		State(int option) {
			this.option = option;
		}

		static State fromNumber(int option) {
            switch (option) {
                case 0: return CHOOSE;
                case 1: return CREATE_PASS;
                case 2: return READ_PASS;
                case 3: return DEL_PASS;
                case 4: return SAVE;
                case 5: return EXIT;
                default: throw new IllegalArgumentException("Invalid option: " + option);
            }
        }
	}
}
