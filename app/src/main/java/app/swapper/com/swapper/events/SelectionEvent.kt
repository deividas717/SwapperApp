package app.swapper.com.swapper.events

import app.swapper.com.swapper.State

data class SelectionEvent(val isEmpty: Boolean, val state: State)