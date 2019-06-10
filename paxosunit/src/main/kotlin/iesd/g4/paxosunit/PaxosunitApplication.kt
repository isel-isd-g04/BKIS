package iesd.g4.paxosunit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaxosunitApplication

fun main(args: Array<String>) {
	runApplication<PaxosunitApplication>(*args)
}
