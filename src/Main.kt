import authentication.AuthenticationService
import route.RouteService
import java.util.concurrent.Executors

class Main {

    companion object {

        private val BASE_URL = "http://localhost:5000"
        private val REGISTRATION_ENDPOINT = "/register"

        private val authenticationService = AuthenticationService()
        private val threadPoolExecutor = Executors.newFixedThreadPool(5)

        @JvmStatic
        fun main(args: Array<String>) {

            val accessToken = authenticationService.getAuthenticationToken("${this.BASE_URL}${this.REGISTRATION_ENDPOINT}")
            val linkList = mutableListOf("/home")
            val iterator = linkList.listIterator()

            val routeService = RouteService(this.BASE_URL, accessToken, this.threadPoolExecutor)

            while (iterator.hasNext()) {
                routeService.getRoutes(iterator.next()).forEach {
                    iterator.add(it)
                    iterator.previous()
                }
            }
        }
    }
}