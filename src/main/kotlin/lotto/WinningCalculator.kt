package lotto

class WinningCalculator {
    fun getWinningMoney(statistics: List<Int>): Int {
        var money = 0

        Rank.values().forEach { rank ->
            money += rank.winningMoney * statistics[rank.ordinal]
        }

        return money
    }
}
