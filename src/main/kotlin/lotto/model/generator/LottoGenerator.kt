package lotto.model.generator

import lotto.model.LottoNumber.Companion.END_LOTTO_RANGE
import lotto.model.LottoNumber.Companion.START_LOTTO_RANGE

class LottoGenerator : NumberGenerator {
    override fun generate(): List<Int> {
        return ((START_LOTTO_RANGE..END_LOTTO_RANGE).shuffled().subList(0, SIZE_OF_LOTTO).sorted())
    }

    companion object {
        private const val SIZE_OF_LOTTO = 6
    }
}
