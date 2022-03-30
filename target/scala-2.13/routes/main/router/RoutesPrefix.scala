// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/alan/Code/Glasgow/ITP/ITSD-DT2022-Template/conf/routes
// @DATE:Sun Mar 06 00:55:28 GMT 2022


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
