package Authentication

interface AuthenticationRepository {

    fun getAuthenticationToken(url: String) : String
}