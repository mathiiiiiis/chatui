package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"net"
	"os"
	"strings"

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
	user := scanner.Text()
	msg := models.Message{
		Type: "register",
		Content: user,
	}
	data, err := json.Marshal(msg)
	fmt.Fprintln(conn, string(data))

	line, err := reader.ReadString('\n')
	fmt.Println(line)

	go eventListener(reader)

	for scanner.Scan() {
		input := scanner.Text()
		msg := inputParser(input, user)
		data, _ := json.Marshal(msg)
		fmt.Fprintln(conn, string(data))
	}
}

//listens for messages from the server
func eventListener(reader *bufio.Reader) {
	for {
		line, err := reader.ReadString('\n')
		if err != nil {
			return
		}
		fmt.Println(line)
	}
}

//takes plain input and transforms into JSON
func inputParser(input string, user string) models.Message {
	var msg models.Message

	if strings.HasPrefix(input, "/join") {
		parts := strings.Split(input, " ")
		msg = models.Message{
			Type: "room.join",
			Content: parts[1],
			User: user,
		}
	} else if strings.HasPrefix(input, "/leave") {
		msg = models.Message{
			Type: "room.leave",
		}
	} else {
		msg = models.Message{
			Type: "room.message",
			Content: input,
			User: user,
		}
	}
	return msg
}
