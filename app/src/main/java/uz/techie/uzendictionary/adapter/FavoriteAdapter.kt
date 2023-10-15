package uz.techie.uzendictionary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.databinding.AdapterFavoriteBinding
import uz.techie.uzendictionary.models.Favorite
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionary.utils.Constants

class FavoriteAdapter(private val listener: WordListener):RecyclerView.Adapter<FavoriteAdapter.WordViewHolder>() {

    private val TAG = "WordAdapter"
    private var primaryLang = Constants.LANG_UZBEK

    private val diffCallBack = object :DiffUtil.ItemCallback<Favorite>(){
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem.docId == newItem.docId
        }

        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class WordViewHolder(val binding:AdapterFavoriteBinding):RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = AdapterFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val favorite = differ.currentList[position]

        if (primaryLang == Constants.LANG_UZBEK){
            holder.binding.adapterFavoriteTitle.text = favorite.word_uz
        }
        else{
            holder.binding.adapterFavoriteTitle.text = favorite.word_en
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(favorite)
        }
        holder.binding.adapterFavoriteDelete.setOnClickListener {
            listener.onDeleteClick(favorite)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface WordListener{
        fun onItemClick(favorite: Favorite)
        fun onDeleteClick(favorite: Favorite)
    }

    fun changeLang(lang:String){
        primaryLang = lang
        notifyDataSetChanged()
    }

}