package ir.fallahpoor.contentprovider

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.UserDictionary
import android.util.Log
import androidx.cursoradapter.widget.SimpleCursorAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        insertWordButton.setOnClickListener {
            insertWord()
        }

        displayUserDictionary()

    }

    private fun displayUserDictionary() {

        val wordsCursor: Cursor? = queryUserDictionaryContentProvider()

        when (wordsCursor?.count) {
            null -> {
                // There is some error. Cursor is null in here
                logMessage("Cursor is null")
            }
            0 -> {
                // There is no data
                logMessage("There is no data")
            }
            else -> {
                val cursorAdapter = createSimpleCursorAdapter(wordsCursor)
                wordsListView.adapter = cursorAdapter
            }

        }

    }

    private fun queryUserDictionaryContentProvider(): Cursor? {

        val projection: Array<String> = arrayOf(
            UserDictionary.Words._ID,
            UserDictionary.Words.WORD,
            UserDictionary.Words.LOCALE
        )
        val selectionClause: String? = null
        val selectionArgs = emptyArray<String>()
        val sortOrder = "ASC"

        /*
         * The method to use for reading from a ContentProvider is query().
         */
        return contentResolver.query(
            /*
             * The URI, using the content:// scheme, for the content to retrieve.
             * In relational database terms, URI is the table to read data from.
             */
            UserDictionary.Words.CONTENT_URI,
            /* A list of which columns to return. Passing null will return all columns,
             * which is inefficient.
             */
            projection,
            /*
             * A filter declaring which rows to return, formatted as an
             * SQL WHERE clause (excluding the WHERE itself). Passing null will return
             * all rows for the given URI.
             */
            selectionClause,
            /*
             * You may include ?s in selectionClause, which will be
             * replaced by the values from selectionArgs, in the order that they
             * appear in the selection. The values will be bound as Strings.
             */
            selectionArgs,
            /*
             * How to order the rows, formatted as an SQL ORDER BY
             * clause (excluding the ORDER BY itself). Passing null will use the
             * default sort order, which may be unordered.
             */
            sortOrder
        )

    }

    private fun createSimpleCursorAdapter(wordsCursor: Cursor): SimpleCursorAdapter {

        val wordListColumns: Array<String> = arrayOf(
            UserDictionary.Words.WORD,
            UserDictionary.Words.LOCALE
        )
        val wordListItems = intArrayOf(R.id.wordTextView, R.id.localeTextView)

        return SimpleCursorAdapter(
            this,
            R.layout.word_list_row,
            wordsCursor,
            wordListColumns,
            wordListItems,
            0
        )

    }

    private fun insertWord() {

        val newValues = ContentValues().apply {
            put(UserDictionary.Words.APP_ID, "ir.fallahpoor.contentprovider")
            put(UserDictionary.Words.LOCALE, "en_US")
            put(UserDictionary.Words.WORD, "insert")
            put(UserDictionary.Words.FREQUENCY, "100")
        }

        val uri: Uri? = contentResolver.insert(
            UserDictionary.Words.CONTENT_URI,
            newValues
        )

        if (uri == null) {
            logMessage("Could not write to User Dictionary")
        }

    }

    private fun updateWord() {
        val updateValues = ContentValues().apply {
            putNull(UserDictionary.Words.LOCALE)
        }
        val selectionClause: String = UserDictionary.Words.LOCALE + "LIKE ?"
        val selectionArgs: Array<String> = arrayOf("en_%")
        var rowsUpdated = 0

        rowsUpdated = contentResolver.update(
            UserDictionary.Words.CONTENT_URI,
            updateValues,
            selectionClause,
            selectionArgs
        )

    }

    private fun deleteWord() {

        val selectionClause = "${UserDictionary.Words.LOCALE} LIKE ?"
        val selectionArgs: Array<String> = arrayOf("user")
        var rowsDeleted = 0

        rowsDeleted = contentResolver.delete(
            UserDictionary.Words.CONTENT_URI,
            selectionClause,
            selectionArgs
        )

    }

    private fun logMessage(message: String) {
        Log.d("@@@@@@", message)
    }

}