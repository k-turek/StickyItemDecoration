# StickyItemDecoration

https://user-images.githubusercontent.com/12100947/172124916-88d68d59-27ed-46f2-ace4-1a9a13d2b3a0.mp4

# Usage

It's not necessary to create a sticky item as RecyclerView is initializing. You can add it after you create an adapter and submit some items, because it's just an item decoration.

First, create a view and bind some data with it. Create an item decoration and add it to the recyclerView wherever you want.

```
val view = layoutInflater.inflate(R.layout.view_holder, recyclerView, false)
val stickyView = ViewHolder(view).apply { bind(stickyItem) }.itemView
val stickyItemDecoration = StickyItemDecoration(stickyView, stickyItem.adapterPosition)
recyclerView.addItemDecoration(stickyItemDecoration)
```
