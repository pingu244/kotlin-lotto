package lotto.domain

import lotto.model.Lotto
import lotto.model.LottoNumber
import lotto.model.ManualLotto
import lotto.model.Money
import lotto.model.UserLotto
import lotto.model.WinningLotto
import lotto.model.generator.UserLottoGenerator
import lotto.view.ERROR_INSERT_AGAIN
import lotto.view.InputView
import lotto.view.OutputView.printMessage
import lotto.view.OutputView.printPurchaseMessage
import lotto.view.OutputView.printResult
import lotto.view.OutputView.printUserLotto

class LottoController(
    private val userLottoGenerator: UserLottoGenerator = UserLottoGenerator(),
) {
    fun start() {
        val money = getMoney()
        val numberOfLotto = money.getNumberOfLotto()
        val manualLotto = getNumberOfManualLotto(numberOfLotto)
        printPurchaseMessage(manualLotto, numberOfLotto)
        val myLotto = userLottoGenerator.generateLotto(
            getManualLotto(manualLotto.numberOfLotto),
            numberOfLotto - manualLotto.numberOfLotto
        )
        printUserLotto(myLotto)
        wrapUpResult(myLotto, money)
    }

    private fun getMoney(): Money {
        printMessage(INSERT_MONEY)
        return validateInput {
            InputView.getNumber()?.let { Money(it) }
        } ?: getMoney()
    }

    private fun getNumberOfManualLotto(numberOfLotto: Int): ManualLotto {
        printMessage(INSERT_MANUAL_LOTTO_NUMBER)
        return validateInput {
            InputView.getNumber()?.let { ManualLotto(it).validateNumberOfLotto(numberOfLotto) }
        } ?: getNumberOfManualLotto(numberOfLotto)
    }

    private fun getManualLotto(numberOfLotto: Int): List<Lotto> {
        val manualLotto = mutableListOf<Lotto>()
        if (numberOfLotto == 0)
            return manualLotto
        printMessage(INSERT_MANUAL_LOTTO)
        repeat(numberOfLotto) {
            manualLotto.add(getLottoNumber())
        }
        return manualLotto
    }

    private fun wrapUpResult(myLotto: UserLotto, money: Money) {
        printMessage(INSERT_WINNING_NUMBER)
        val winningLotto = getWinningLotto(getLottoNumber())
        val ranks = myLotto.getWinningStatistics(winningLotto)
        val rates = WinningCalculator.getEarningRate(money, WinningCalculator.getWinningMoney(ranks))
        printResult(ranks, rates)
    }

    private fun getWinningLotto(lotto: Lotto): WinningLotto {
        return validateInput {
            WinningLotto(lotto, getBonusNumber())
        } ?: getWinningLotto(lotto)
    }

    private fun getBonusNumber(): LottoNumber {
        printMessage(INSERT_BONUS_BALL)
        return validateInput {
            InputView.getNumber()?.let { LottoNumber.from(it) }
        } ?: getBonusNumber()
    }

    private fun getLottoNumber(): Lotto {
        return validateInput {
            InputView.getNumberList()?.let { Lotto(*it.toIntArray()) }
        } ?: getLottoNumber()
    }

    private fun <T> validateInput(create: () -> T): T? {
        return runCatching {
            create()
        }.onSuccess {
            if (it == null)
                println(ERROR_INSERT_AGAIN)
        }.onFailure {
            println(it.message)
        }.getOrNull()
    }

    companion object {
        private const val INSERT_MONEY = "구입금액을 입력해 주세요."
        private const val INSERT_MANUAL_LOTTO_NUMBER = "\n수동으로 구매할 로또 수를 입력해 주세요."
        private const val INSERT_MANUAL_LOTTO = "\n수동으로 구매할 번호를 입력해 주세요."
        private const val INSERT_WINNING_NUMBER = "\n지난 주 당첨 번호를 입력해 주세요."
        private const val INSERT_BONUS_BALL = "보너스 볼을 입력해 주세요."
    }
}
