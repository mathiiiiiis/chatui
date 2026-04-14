package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"net"
	"os"

	"chatui/client/models"
)

func main()  {
	//connect to server
	conn, err := net.Dial("tcp", "localhost:5000")
	if err != nil {
		fmt.Println(err)
		return
	}
	defer conn.Close()
	reader := bufio.NewReader(conn)

	//register  prompt
	fmt.Println("Enter username: ")
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Scan()
	input := scanner.Text()
	msg := models.Message{
		Type: "register",
		Content: input,
	}
	data, err := json.Marshal(msg)
	fmt.Fprintln(conn, string(data))

	line, err := reader.ReadString('\n')
	fmt.Println(line)

	go eventListener(conn)

	for scanner.Scan() {
		input := scanner.Text()
		fmt.Fprintln(conn, string(input))
	}
}

//listens for messages from the server
func eventListener(conn net.Conn) {
	scanner := bufio.NewScanner(conn)
	for scanner.Scan() {
		line := scanner.Text()
		fmt.Println(line)
	}
}
