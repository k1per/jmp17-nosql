package com.epam.learning.aykorenev.nosql;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

/*
Разверните MongoDB.
Создайте простейшее консольное приложение которое сохраняет ваши заметки в БД.
Заметка содержит 3 поля: Дата создания, Тег для записи, Текст записи.
Например: 10.13.2016, "Домашние дела", "Нужно купить молоко"

Организуйте выборку из базы данных и вывод в консоль:
1 Все записи
2 По тегу
3 Полнотекстовый поиск по 2 и 3 полю.

Добавьте возможность удаления найденных записей.
 */
public class Main {

    private static MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
    private static MongoDatabase database;
    private static MongoCollection<Document> notes;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws ParseException {
        database = mongoClient.getDatabase("mydatabase");
        notes = database.getCollection("notes");

        System.out.println("To exit -> exit. To add new note -> add. To show all -> show. To delete - delete id");
        String command = scanner.nextLine();
        while (!command.equals("exit")){
            switch (command){
                case "add" :
                    add();
                    break;
                case "show" :
                    showAll();
                    break;
                case "delete" :
                    delete();
                    break;
                case "search" :
                    search();
                    break;
                default:
                    System.out.println("Unknown command");
            }
            System.out.println("To exit -> exit. To add new note -> add. To show all -> show. To delete - delete id." +
                    "To search -> search");
            command = scanner.nextLine();
        }



        System.out.println("Number of nodes" + notes.count());
        for (Document note : notes.find()) {
            System.out.println(note);
        }

    }

    private static void search() {
        System.out.println("Search by tag -> tag, full-text-search -> full, exit -> exit");
        String searchBy = scanner.nextLine();
        while (!searchBy.equals("exit")) {
            switch (searchBy) {
                case "tag":
                    searchByTag();
                    break;
                case "full":
                    fullTextSearch();
                    break;
                default:
                    System.out.println("Unknown commmand");
            }
            System.out.println("To exit -> exit, Search by tag -> tag, full-text-search -> full");
            searchBy = scanner.nextLine();
        }
    }

    private static void fullTextSearch() {
        Block<Document> printBlock = document -> System.out.println(document.toJson());
        System.out.println("Provide word to search");
        String wordToSearch = scanner.nextLine();
         notes.find(Filters.text(wordToSearch)).forEach(printBlock);
    }

    private static void searchByTag() {
        Block<Document> printBlock = document -> System.out.println(document.toJson());
        System.out.println("Please provide tag to search: ");
        String tag = scanner.nextLine();
        notes.find(eq("tag", tag)).forEach(printBlock);
    }

    private static void delete() {
        System.out.println("Provide id to delete from notes");
        notes.deleteOne(new Document("_id", new ObjectId(scanner.nextLine())));
    }

    private static void showAll() {
        System.out.println("Number of notes " + notes.count());
        for (Document note : notes.find()) {
            System.out.println(note);
        }
    }

    private static void add() throws ParseException {
        Document document = new Document();
        System.out.println("Insert date yyyy.MM.dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        document.put("date", dateFormat.parse(scanner.nextLine()));
        System.out.println("Insert tag");
        document.put("tag", scanner.nextLine());
        System.out.println("Insert note");
        document.put("note", scanner.nextLine());
        notes.insertOne(document);
    }

}
