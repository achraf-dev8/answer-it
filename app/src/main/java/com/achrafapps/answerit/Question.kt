package com.achrafapps.answerit

class Question {

    var subject = ""
    var ans1 = ""
    var ans2= ""
    var ans3= ""
    var ans4= ""
    var right= 1

    constructor(subject:String, ans1:String, ans2:String, ans3:String, ans4:String, right:Int){

        this.subject = subject
        this.ans1 = ans1
        this.ans2 = ans2
        this.ans3 = ans3
        this.ans4 = ans4
        this.right = right

    }



}