package com.krosum.demo;

public class TextBlockDemo {
	public static void main(String[] args) {
		String html = """
		        <html>
		            <body>
		                <p>Hello, World!</p>
		            </body>
		        </html>
		        """;

		System.out.println(html);

		String text = """
                This is the first line.
                To show a newline character like \\n literally, you escape the backslash.
                This is the third line.
                """;
                
        System.out.println(text);
		
	}
}
