package com.nglauber.architecture_sample.books.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nglauber.architecture_sample.books.R
import com.nglauber.architecture_sample.books.viewmodel.BookListViewModel
import com.nglauber.architecture_sample.core.ResultState
import com.nglauber.architecture_sample.core_android.ui.components.AsyncData
import com.nglauber.architecture_sample.core_android.ui.components.GenericError
import com.nglauber.architecture_sample.core_android.ui.theme.BookAppTheme
import com.nglauber.architecture_sample.core_android.ui.theme.custom.AppTheme
import com.nglauber.architecture_sample.domain.entities.Book
import de.charlex.compose.RevealDirection
import de.charlex.compose.RevealSwipe
import com.nglauber.architecture_sample.core_android.R as CoreR

@ExperimentalMaterialApi
@Composable
fun BookListScreen(
    viewModel: BookListViewModel,
    onNewBookClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit
) {
    val booksListState by viewModel.booksListState.collectAsState()
    BooksListContent(
        booksListState = booksListState,
        onNewBookClick = onNewBookClick,
        onLogoutClick = onLogoutClick,
        onSettingsClick = onSettingsClick,
        onBookClick = onBookClick,
        onDeleteBook = viewModel::remove,
        reloadBooks = viewModel::loadBooks
    )
}

@ExperimentalMaterialApi
@Composable
private fun BooksListContent(
    booksListState: ResultState<List<Book>>,
    onNewBookClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onDeleteBook: (Book) -> Unit,
    reloadBooks: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = stringResource(CoreR.string.app_name))
        }, actions = {
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                Icon(Icons.Filled.MoreVert, "More options")
                DropdownMenu(expanded = menuExpanded, onDismissRequest = {
                    menuExpanded = false
                }, content = {
                    DropdownMenuItem(onClick = {
                        menuExpanded = false
                        onSettingsClick()
                    }, content = {
                        Text(stringResource(CoreR.string.menu_action_settings))
                    })
                    DropdownMenuItem(onClick = {
                        menuExpanded = false
                        onLogoutClick()
                    }, content = {
                        Text(stringResource(CoreR.string.menu_action_logout))
                    })
                })
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = onNewBookClick) {
            Icon(
                imageVector = ImageVector.vectorResource(id = CoreR.drawable.ic_add),
                contentDescription = stringResource(
                    id = R.string.cd_new_book
                )
            )
        }
    }) {
        AsyncData(resultState = booksListState, errorContent = {
            GenericError(
                onDismissAction = reloadBooks
            )
        }) { booksList ->
            booksList?.let {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(booksListState is ResultState.Loading),
                    onRefresh = reloadBooks,
                ) {
                    if (it.isEmpty()) {
                        EmptyList()
                    } else {
                        LazyColumn {
                            items(booksList) { item ->
                                BookItem(
                                    book = item,
                                    onBookClick = onBookClick,
                                    onDeleteBook = onDeleteBook,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun BookItem(
    book: Book,
    onBookClick: (Book) -> Unit,
    onDeleteBook: (Book) -> Unit,
) {
    RevealSwipe(backgroundCardModifier = Modifier.padding(8.dp),
        directions = setOf(RevealDirection.EndToStart),
        hiddenContentEnd = {
            IconButton(onClick = { onDeleteBook(book) }) {
                Icon(
                    modifier = Modifier.padding(horizontal = 25.dp),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
        }) {
        Card(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    onBookClick(book)
                },
            elevation = 2.dp,
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .size(bookCoverSize)
                        .align(Alignment.CenterVertically)
                        .background(AppTheme.colors.primary.copy(alpha = .3f))
                ) {
                    if (book.coverUrl == "") {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = CoreR.drawable.ic_no_photo),
                            contentDescription = null,
                            tint = AppTheme.colors.primary,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(48.dp)
                        )
                    }
                    Image(
                        painter = rememberAsyncImagePainter(book.coverUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(bookCoverSize)
                            .align(Alignment.CenterStart)
                    )
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(book.title, style = AppTheme.typography.h5)
                    Text(book.author)
                    Text(stringResource(id = R.string.text_format_book_year, book.year))
                    Text(stringResource(id = R.string.text_format_book_pages, book.pages))
                }
            }
        }
    }
}

@Composable
fun EmptyList() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(id = R.string.msg_empty_books_list))
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun PreviewBookListContent() {
    BookAppTheme {
        BooksListContent(
            booksListState = ResultState.Success(
                listOf(bookForUiPreview())
            ),
            onNewBookClick = {},
            onLogoutClick = {},
            onSettingsClick = {},
            onBookClick = {},
            onDeleteBook = {},
            reloadBooks = {},
        )
    }
}