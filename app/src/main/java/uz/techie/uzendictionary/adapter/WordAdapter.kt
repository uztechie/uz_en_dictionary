package uz.techie.uzendictionary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_word.view.*
import uz.techie.uzendictionary.R
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


    inner class WordViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = differ.currentList[position]

        if (primaryLang == Constants.LANG_UZBEK){
            holder.itemView.adapter_title.text = word.word_uz
        }
        else{
            holder.itemView.adapter_title.text = word.word_en
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