# CS-499_CompSci_Capstone
This is the Login activity of my Inventory application rewritten in Kotlin and following best practices.

## Application Overview
This Login application is a general-purpose app that can be extended for use in any other application that uses a login screen. It accepts a user's email address and password. If the user has not stored the input email in the database, they can input a password and store the combination securely using encryption to the database. If the user already has an account stored, they can login with the Login button.

Here is the Login activity:

![KotlinLoginFirstPic](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/83f89680-47c3-4702-afc3-7166fe9a7b39)



A valid Login is determined using the following Query that returns a Boolean specifying if the username and password combination was found or not:

```
// Determine if a username already exists in the database; used to confirm storage
@Query("SELECT EXISTS(SELECT * FROM Login WHERE username = :username)")
suspend fun usernameExists(username: String): Boolean
```

When the user presses either the Login or Create Account buttons, the input email and password are first checked to see if they follow their respective patterns. If the input username doesn't look like an email, the email input will be erased, the email text field will turn red, and output a supporting text message letting the user know that the email should follow an email pattern. If the password doesn't follow secure password guidelines such as using uppercase and lowercase letters, symbols, numbers, and longer passwords, the input password will be erased, the password text field will turn red, and output a supporting text message letting the user know that their password should follow certain guidelines:

![KotlinLoginErrorPic](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/2eae135c-ad0d-4fe4-b6f5-e3020c6ec8fe)

If the user tries to create an account using an email already stored in the database, they will receive a Toast with a message stating "Account creation failed.":

![KotlinAccountCreationFailed](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/ad47bc1a-0d2c-4b45-a89f-cd59ba6326fa)


If the user tries to login with an email/password combination stored in the database, they will receive a Toast with a message stating "Login not found!":

![KotlinLoginNotFound](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/d9e16e79-b628-49da-afad-5c4bcad1e778)


Otherwise, they will be navigated to the next screen letting them know that they have logged in after account creation or a login:

![KotlinLoginSuccess](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/cfede820-01f5-4f7e-b01e-ecdbe240e082)

Logins stored are encrypted by the Room database. The CryptoManager class algorithmically generates a secret key for encryption and decryption if one doesn't exist yet:

```
// Initialize the keystore and then load it
        private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        /**
         * Get the secret key, or create a new key if none exists yet.
         */
        fun getKey(): SecretKey {
            val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
            return existingKey?.secretKey ?: createKey()
        }

        /**
         * Create a cryptographic key to store and use by the SQLCipher database
         */
        private fun createKey(): SecretKey {
            return KeyGenerator.getInstance(ALGORITHM). apply {
                init(
                    KeyGenParameterSpec.Builder(
                        "secret",
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(PADDING)
                        .setUserAuthenticationRequired(false)
                        .setRandomizedEncryptionRequired(true)
                        .build()
                )
            }.generateKey()
        }
```


From here, the key is then stored in an encrypted file on the device for the Room database to use for encryption and decryption of stored data:

```
/**
 * Specify the database with the Login class as the entity; without keeping a version backup history
 */
@Database(entities = [Login::class], version = 3, exportSchema = false)
abstract class LoginDatabase : RoomDatabase() {
    // Initialize the LoginDao()
    abstract fun loginDao(): LoginDao
    // Companion object allows access to database methods
    companion object {
        /**
         * Set up an instance of LoginDatabase; annotated Volatile to ensure all reads and writes
         * come from the main memory and is always up-to-date
         */
        @Volatile
        private var Instance: LoginDatabase? = null

        /**
         * Return the instance of the database; otherwise, build the database with the
         * synchronized block to ensure only one instance of the database can be created.
         */
        fun getDatabase(context: Context): LoginDatabase {
            // Initialize/write a generated encrypted key to the encrypted file
            writeEncryptedFile(context, CryptoManager.getKey())
            val passphrase = readEncryptedFile(context)
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = LoginDatabase::class.java,
                    name = "login_database"
                )
                    // Implement SQLCipher to encrypt the database
                    .openHelperFactory(SupportFactory(passphrase))
                    // Simply destroy and recreate to migrate later if needed.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
```


## Final Enhancement Plan

### Category 1: Software Engineering/Design

a. the artifact name – The onHand Inventory application was created for CS-360: Mobile Architecture and Design in the Java and XML programming languages. It can be found here on GitHub: https://github.com/trevor-leon/CS-360_Mobile_Arch_and_Programming.

b. the enhancement plan – First, I want to convert or rewrite the login screen of the project in the Kotlin programming language using Jetpack Compose and following best practices so that it can be reused for any application. Best practices of development with Kotlin for Android include utilizing Coroutines for asynchronous tasks such as database access; Flows and unidirectional data flow (UDF) to get updated data from the user interface (UI) and updated UI; dependency injection for better code reusability; I will primarily be following the official Android courses here using Kotlin and Jetpack Compose to upgrade my project to the currently recommended toolkits. I lastly want to include a dark/light mode functionality using the guide found here: https://developer.android.com/develop/ui/views/theming/darktheme. 

c. the specific skills relevant to the course outcomes – In this project, I want to employ strategies for building collaborative environments by utilizing separation of concerns to allow for a more collaborative and scalable project and provide useful comments throughout the project. Additionally, I will showcase my newfound Kotlin skills in this project to show I can utilize innovative solutions as the course outcomes require. Also, a dark/light mode will allow more flexibility and a wider audience to enjoy my app according to their own preference. Below 
is the overall structure and plan of the Login activity:

![image](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/8e80b9fd-79cb-4f56-9098-b939101a9fa7)

 
&emsp;When the app is started, the onCreate() method is called, which sets up the screen as shown above. A new user can input their username and password before tapping the “Create Account” button, which will save their credentials to the database securely. If the username is already in the database, it will not be inserted, and the user will be notified of an invalid entry. If the user taps the “Login” button, the input credentials will be verified against the credentials in the database, and the user will be directed accordingly. From here, the Login activity can be reused for any other application.

 
## Category 2: Algorithms and Data Structures

a. the artifact name - The onHand Inventory application was created for CS-360: Mobile Architecture and Design in the Java and XML programming languages.

b. the enhancement plan – The Login and Create Account methods I already designed do not properly salt/encrypt data before use since I learned about that concept later in my studies. Before the user’s email and password are stored in the database, they should be properly encrypted using recommended encryption algorithms before storing the user data. To accomplish this, I will use a CryptoManager class and the SQLCipher class to implement encryption for the Room database. The basic data structure of a Login should also be considered. For example, every user account will contain an id, username, and password. Lastly, the emails and passwords should be checked for email and password patterns. For example, there is a Patterns class that contains an email address pattern that can be used to check the input username string.

c. the specific skills relevant to the course outcomes – I will demonstrate that I can design and evaluate computing solutions that solve a given problem using algorithmic principles and computer science practices and standards appropriate to its solution by properly utilizing encryption algorithms to better protect user data and checking input fields for proper input. Encrypting user data before storing it also demonstrates a security mindset that anticipates adversarial exploits in software architecture and designs to expose potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced security of data and resources by making it considerably harder for an attacker to use the encrypted data.

![image](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/d76d3d77-579f-4295-ab6c-39185ebcc6de)

 
&emsp;The UI sends loginUiState data such as the input username and password to the ViewModel, which determines if it is valid before storing it or using it. The CryptoManager class provides an encryption key stored in an encrypted file on the device for the Room database configured with SQLCipher to use to encrypt the database. The input is then used by the database’s operations to read or write necessary data. When the user wants to log in or create an account, they will tap the respective button. This will send the current data in the text boxes to the LoginViewModel, which will use database operations to store, read, or manipulate it. Below is example of pseudocode of how the login/create account button functions would be implemented using my plan of pattern matching; as well as how user accounts should be verified before storing them in the database:

```
If User taps Login or Create Account button AND username textbox does NOT match email pattern:
	Turn the textbox RED and output invalid email Toast
If User taps Login or Create Account button AND password textbox text does NOT match secure password pattern (x amount of characters; using lowercase letters, uppercase letters, and at least one number and symbol)
	Turn the textbox RED and show output text explaining password rules
	
Else
	Login
```

```
If User taps Create Account button:
	Verify username/password combination pattern
	Salt/encrypt username
	Salt/encrypt password
	Verify username/password combination against Login database
	If username/password combination incorrect:
		Create a Toast stating invalid account creation
	Else:
		Login

If User taps Login button:
	Salt/encrypt username
	Salt/encrypt password
	Verify username/password combination against Login database
	If username/password combination incorrect:
		Create a Toast stating invalid login
	Else:
		Login
```
  
### Category 3: Databases

a. the artifact name - The onHand Inventory application was created for CS-360: Mobile Architecture and Design in the Java and XML programming languages.

b. the enhancement plan – Firstly, I want to utilize a Room database, which is the recommended way to use SQL/SQLite databases with Jetpack Compose. I want to add specific IDs to each user in the database as primary keys, rather than only using the user’s unique username as done in the previous implementation. To accomplish this, I will follow the recommended Room guidelines defined here to implement the pipeline of components such as the database itself; data access objects (DAOs), and entities to represent individual Logins within the database.

c. the specific skills relevant to the course outcomes – I want to showcase my ability to use well-founded and innovative techniques, skills, and tools in computing practices for the purpose of implementing computer solutions that deliver value and accomplish industry-specific goals by using Room databases. As previously stated, Room databases are recommended to use as they serve as an abstraction layer to SQLite databases to prevent misuse and simplify the process. Doing so also demonstrates a security mindset that anticipates adversarial exploits in software architecture and designs to expose potential vulnerabilities, mitigate design flaws, and ensure privacy and enhanced security of data and resources as they are designed to verify queries, reduce boilerplate code that comes with using SQLite.

![image](https://github.com/trevor-leon/CS-499_CompSci_Capstone/assets/72781990/26f57160-9c4e-4e7c-b1e0-badebcf4369b)

 
# ePortfolio Overall

&emsp;The skills I want to demonstrate for this ePortfolio are my software development skills including algorithms and data structure, my user experience design skills, and my secure coding skills. It’s crucial to design software with all kinds of users in mind in today’s world, and that’s what I hope to achieve. I believe I managed to cover each required course outcome in my Enhancement Plan above. I have a foundational knowledge of Kotlin that I am working on, and I am excited to learn more about it! In the end, I hope that my plans for enhancement are not too ambitious. It’s certainly very complex, and I hope it doesn’t lead to any errors down the line.

## Known Issue

&emsp;The only issue I am currently aware of is that the theme is not extended to the Login Success Activity. It is only applied to the Login Activity despite the entire App being defined to use the KotlinLoginTheme:

```
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinLoginTheme {
                // A surface container using the 'background' color from the theme
                LoginApp()
            }
        }
    }
}
```
