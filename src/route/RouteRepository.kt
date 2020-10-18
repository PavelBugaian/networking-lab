package route

interface RouteRepository {

    fun getRoutes(endpoint : String) : ArrayList<String>
}