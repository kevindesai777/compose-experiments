import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import composeexperiments.composeapp.generated.resources.Res
import composeexperiments.composeapp.generated.resources.book1
import composeexperiments.composeapp.generated.resources.book2
import composeexperiments.composeapp.generated.resources.book3
import org.jetbrains.compose.resources.painterResource
import kotlin.math.ceil


@Composable
fun LibbyBookArrangement() {
    Box(
        modifier = Modifier
            .width(600.dp)
            .height(900.dp)
            .background(Color.White)
            .border(4.dp, Color.Black),
        contentAlignment = Alignment.Center
    ) {

        val numOfBooks = booklist.size
        val parentWidth = 600.dp
        val bookSize = 120.dp
        val maxBooksInARow = (parentWidth / bookSize)
        val numRows = ceil(numOfBooks / maxBooksInARow).toInt()
        LazyColumn {
            itemsIndexed((0..<numRows).map { it.toString() }) { rowIndex, row ->
                print("on row number: $rowIndex")
                val booksInThisRow = if (rowIndex % 2 == 0)
                    maxBooksInARow
                else
                    maxBooksInARow - 1
                print("books in this row will be $booksInThisRow")
                LazyRow(
                    modifier =
                        Modifier.fillMaxWidth()
                            .offset {
                                when {
                                    rowIndex == 0 -> IntOffset(0, 0)
                                    rowIndex%2 == 0 -> IntOffset(0, -(30+(rowIndex*15)))
                                    rowIndex%2 !=0 -> IntOffset(90, -(30+(rowIndex*15)))
                                    else -> IntOffset(0,0)
                                }
                            }
                ) {
                    itemsIndexed((0..<booksInThisRow.toInt()).map { it.toString() }) { rowBookIndex, rowBook ->
                        print("rowBookIndex is $rowBookIndex")
                        val bookIndex =
                            (((rowIndex * maxBooksInARow) - rowIndex / 2) + rowBookIndex).toInt()
                        print("nowBookIndexIs: $bookIndex")
                        Image(
                            modifier = Modifier
                                .size(bookSize)
                                .rotate(
                                    if (rowIndex % 2 == 0) {
                                        45f
                                    } else -45f
                                ),
                            painter = painterResource(booklist[bookIndex]),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}

val booklist = listOf(
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3,
    Res.drawable.book1,
    Res.drawable.book2,
    Res.drawable.book3
)