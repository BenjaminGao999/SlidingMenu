package com.qingmu.quickindex;

public class Person implements Comparable<Person>{

	private String name;
	private char letter;

	public Person(String name, char letter) {
		this.name = name;
		this.letter = letter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", letter=" + letter + "]";
	}

	@Override
	public int compareTo(Person o) {
		return letter - o.letter;
	}

}
