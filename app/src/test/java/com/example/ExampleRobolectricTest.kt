package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.LevelRepository
import com.example.engine.MatchDetector
import com.example.model.BoardTile
import com.example.model.Candy
import com.example.model.CandyColor
import com.example.model.SpecialType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TJ Sugar Quest", appName)
  }

  @Test
  fun `verify levels catalog exists`() {
    val levels = LevelRepository.getAllLevels()
    assertTrue(levels.size >= 25)
    val level1 = LevelRepository.getLevel(1)
    assertEquals(1, level1.levelNumber)
    assertEquals("Candy Town", level1.worldName)
  }

  @Test
  fun `verify horizontal 3 match detection`() {
    val board = Array(8) { r ->
      Array(8) { c ->
        BoardTile(row = r, col = c, candy = Candy(color = CandyColor.BLUE))
      }
    }
    // Set 3 red in a row
    board[2][2] = board[2][2].copy(candy = Candy(color = CandyColor.RED))
    board[2][3] = board[2][3].copy(candy = Candy(color = CandyColor.RED))
    board[2][4] = board[2][4].copy(candy = Candy(color = CandyColor.RED))

    val result = MatchDetector.findMatches(board)
    assertTrue(result.matchedPositions.contains(Pair(2, 2)))
    assertTrue(result.matchedPositions.contains(Pair(2, 3)))
    assertTrue(result.matchedPositions.contains(Pair(2, 4)))
  }
}
