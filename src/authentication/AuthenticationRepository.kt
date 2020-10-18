package authentication

interface AuthenticationRepository {

    fun getAuthenticationToken(url: String) : String
}