package org.example.beans

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AreaCheckBeanTest {

    private val bean = AreaCheckBean()

    // checkPoint

    @Test
    fun `точка в круге первый квадрант`() {
        val result = bean.checkPoint(0.5, 0.5, 2.0)
        assertTrue(result.result)
    }

    @Test
    fun `точка вне круга первый квадрант`() {
        val result = bean.checkPoint(2.0, 2.0, 1.0)
        assertFalse(result.result)
    }

    @Test
    fun `точка в прямоугольнике третий квадрант`() {
        val result = bean.checkPoint(-1.0, -0.5, 2.0)
        assertTrue(result.result)
    }

    @Test
    fun `точка вне прямоугольника третий квадрант`() {
        val result = bean.checkPoint(-3.0, -2.0, 2.0)
        assertFalse(result.result)
    }

    @Test
    fun `точка в треугольнике второй квадрант`() {
        val result = bean.checkPoint(-0.5, 0.1, 2.0)
        assertTrue(result.result)
    }

    @Test
    fun `точка вне треугольника второй квадрант`() {
        val result = bean.checkPoint(-0.5, 2.0, 2.0)
        assertFalse(result.result)
    }

    @Test
    fun `точка в четвертом квадранте всегда промах`() {
        val result = bean.checkPoint(1.0, -1.0, 2.0)
        assertFalse(result.result)
    }

    // validateInputStrings

    @Test
    fun `валидные строки проходят валидацию`() {
        assertTrue(bean.validateInputStrings("1.5", "2.0", "3.0"))
    }

    @Test
    fun `запятая вместо точки проходит валидацию`() {
        assertTrue(bean.validateInputStrings("1,5", "2,0", "2,0"))
    }

    @Test
    fun `текст не проходит валидацию`() {
        assertFalse(bean.validateInputStrings("abc", "1.0", "2.0"))
    }

    @Test
    fun `x вне диапазона не проходит`() {
        assertFalse(bean.validateInputStrings("6.0", "1.0", "2.0"))
    }

    @Test
    fun `r вне диапазона не проходит`() {
        assertFalse(bean.validateInputStrings("1.0", "1.0", "5.0"))
    }

    @Test
    fun `слишком много знаков после запятой не проходит`() {
        assertFalse(bean.validateInputStrings("1.1234423", "1.0", "2.0"))
    }
}