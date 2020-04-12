package com.dripr.dripr.entities.payloads

class SearchData(private var keyWord: String, private var priceRange: Int?, private var numberParticipant: Int?, private var category: String?) {
    fun toUrl(): String {
        var str = ""
        str += "title=" + this.keyWord

        if(this.priceRange != null) {
            str += "priceRange=" + this.priceRange.toString()
        }

        if(this.numberParticipant != null) {
            str += "priceRange=" + this.priceRange.toString()
        }

        if(this.category != null) {
            str += "category=" + this.category.toString()
        }

        return "?$str"
    }
}