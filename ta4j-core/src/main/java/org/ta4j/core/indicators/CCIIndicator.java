/*******************************************************************************
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2014-2017 Marc de Verdelhan, 2017-2018 Ta4j Organization 
 *   & respective authors (see AUTHORS)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package org.ta4j.core.indicators;

import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.statistics.MeanDeviationIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.BarSeries;

/**
 * Commodity Channel Index (CCI) indicator.
 * <p/>
 *
 * @see <a href="http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:commodity_channel_in">
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:commodity_channel_in</a>
 */
public class CCIIndicator extends CachedIndicator<Num> {

    private final Num FACTOR;
    private final TypicalPriceIndicator typicalPriceInd;
    private final SMAIndicator smaInd;
    private final MeanDeviationIndicator meanDeviationInd;
    private final int barCount;

    /**
     * Constructor.
     *
     * @param series   the bar series
     * @param barCount the bar count (normally 20)
     */
    public CCIIndicator(BarSeries series, int barCount) {
        super(series);
        FACTOR = numOf(0.015);
        typicalPriceInd = new TypicalPriceIndicator(series);
        smaInd = new SMAIndicator(typicalPriceInd, barCount);
        meanDeviationInd = new MeanDeviationIndicator(typicalPriceInd, barCount);
        this.barCount = barCount;
    }

    @Override
    protected Num calculate(int index) {
        final Num typicalPrice = typicalPriceInd.getValue(index);
        final Num typicalPriceAvg = smaInd.getValue(index);
        final Num meanDeviation = meanDeviationInd.getValue(index);
        if (meanDeviation.isZero()) {
            return numOf(0);
        }
        return (typicalPrice.minus(typicalPriceAvg)).dividedBy(meanDeviation.multipliedBy(FACTOR));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " barCount: " + barCount;
    }
}
