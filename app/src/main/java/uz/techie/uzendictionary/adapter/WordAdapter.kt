package uz.techie.uzendictionary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.techie.uzendictionary.R
import uz.techie.uzendictionary.databinding.AdapterFavoriteBinding
import uz.techie.uzendictionary.databinding.AdapterWordBinding
import uz.techie.uzendictionary.models.Word
import uz.techie.uzendictionary.utils.Constants

class WordAdapter(private val listener: WordListener):RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    private val TAG = "WordAdapter"
    private var primaryLang = Constants.LANG_UZBEK

    private val diffCallBack = object :DiffUtil.ItemCallback<Word>(){
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.docId == newItem.docId
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallBack)


    inner class WordViewHolder(val binding: AdapterWordBinding):RecyclerView.ViewHolder(binding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = AdapterWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = differ.currentList[position]

        if (primaryLang == Constants.LANG_UZBEK){
            holder.binding.adapterTitle.text = word.word_uz
        }
        else{
            holder.binding.adapterTitle.text = word.word_en
        }

        holder.itemView.setOnClickListener {
            listener.onItemClick(word)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface WordListener{
        fun onItemClick(word: Word)
    }

    fun changeLang(lang:String){
        primaryLang = lang
        notifyDataSetChanged()
    }

}