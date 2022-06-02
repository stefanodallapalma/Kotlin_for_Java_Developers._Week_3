package taxipark


/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> {
    val realDrivers: Set<Driver> = trips.filter { it.driver in allDrivers }
        .map{ it.driver }
        .toSet()

    return allDrivers - realDrivers
}

/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> =
    allPassengers.associateWith{
        trips.filter { trip -> trip.passengers.contains(it) }
    }.filterValues { trips -> trips.size  >= minTrips }.keys//map { it.key }

/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> {

    val frequentPassengers: List<Passenger> = allPassengers.associateWith{
        trips.filter { trip -> trip.passengers.contains(it) && trip.driver.equals(driver)}
    }.filter { it.value.size > 1 }.map { it.key }

    return frequentPassengers.toSet()
}

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {

    val smartPassengers = allPassengers.associateWith{
        Pair(
            trips.filter { trip -> it in trip.passengers && trip.discount != null}, // Trips with discount
            trips.filter { trip -> it in trip.passengers && trip.discount == null}  // Trips without discount
        )
    }.filter { it.value.first.size > it.value.second.size}.map { it.key }


    return smartPassengers.toSet()
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {

    val allDuration: Set<Int> = trips.map { it.duration }.toSet()
    val maxDuration = allDuration.max() ?: 0

    val periods: ArrayList<IntRange> = ArrayList()
    var i = 0
    while (i < maxDuration){
        periods.add(IntRange(start=i, endInclusive=i+9))
        i += 10
    }

    val periodTripsSizeMap: Map<IntRange, Int> = periods.associateWith {
        trips.filter { trip -> it.contains(trip.duration) }.size
    }

    return periodTripsSizeMap.maxBy { it.value }?.key
}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {

    var totalIncome = trips.sumByDouble { it.cost }

    val driverIncomeMap: Map<Driver, Double> = allDrivers.associateWith {
        trips.filter { trip -> trip.driver.equals(it) }
            .map { trip -> trip.cost }
            .sum()
    }


    val numberTopDrivers = (allDrivers.size * 0.2).toInt()
    var sortedDriverIncomeMap = driverIncomeMap.toList().sortedByDescending { (_, value) -> value }.toMap()
    sortedDriverIncomeMap = sortedDriverIncomeMap.toList().subList(0, numberTopDrivers).toMap()

    return sortedDriverIncomeMap.toList().sumByDouble { it.second } / totalIncome >= 0.8
}