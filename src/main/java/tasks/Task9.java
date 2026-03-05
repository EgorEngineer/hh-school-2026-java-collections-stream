package tasks;

import common.Person;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
Далее вы увидите код, который специально написан максимально плохо.
Постарайтесь без ругани привести его в надлежащий вид
P.S. Код в целом рабочий (не везде), комментарии оставлены чтобы вам проще понять чего же хотел автор
P.P.S Здесь ваши правки необходимо прокомментировать (можно в коде, можно в PR на Github)
 */
public class Task9 {

  private long count;

  // Костыль, эластик всегда выдает в топе "фальшивую персону".
  // Конвертируем начиная со второй
  public List<String> getNames(List<Person> persons) {
    // Проверку на пустой список убрал, так как при пустом persons, stream тоже будет пустым
    // добавил skip(1), вместо удаления из persons
    return persons.stream().skip(1).map(Person::firstName).collect(Collectors.toList());
  }

  // Зачем-то нужны различные имена этих же персон (без учета фальшивой разумеется)
  public Set<String> getDifferentNames(List<Person> persons) {
    // stream с distinct() излишен, т.к. можно сразу исключить дубликаты собрав в Set через конструктор
    return new HashSet<>(getNames(persons));
  }

  // Тут фронтовая логика, делаем за них работу - склеиваем ФИО
  public String convertPersonToString(Person person) {
    // переписал на stream, вместо второго добавления secondName сделал middleName
    // такое решение убирает появление ненужных пробелов при null значениях
    return Stream.of(person.secondName(),person.firstName(),person.middleName())
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));
  }

  // словарь id персоны -> ее имя
  public Map<Integer, String> getPersonNames(Collection<Person> persons) {
    //заменил цикл HashMap + for на stream, функционал сохранился, а строчек стало меньше
    return persons.stream()
        .collect(Collectors.toMap(Person::id, this::convertPersonToString, (previous, current) -> previous));
  }

  // есть ли совпадающие в двух коллекциях персоны?
  public boolean hasSamePersons(Collection<Person> persons1, Collection<Person> persons2) {
    // Заменил вложенный цикл на решение через Set + anyMatch
    // Асимптотика изменилась с O(n*m) на O(n+m)
    Set<Person> personsSet = new HashSet<>(persons2);
    return persons1.stream().anyMatch(personsSet::contains);
  }

  // Посчитать число четных чисел
  public long countEven(Stream<Integer> numbers) {
    // Сделал подсчёт через stream методом count, вместо использования дополнительной переменной.
    return numbers.filter(num -> num % 2 == 0).count();
  }

  // Загадка - объясните почему assert тут всегда верен
  // Пояснение в чем соль - мы перетасовали числа, обернули в HashSet, а toString() у него вернул их в сортированном порядке
  void listVsSet() {
    // В HashSet под капотом HashMap, переданные значения хэшируются и помещаются в бакеты
    // равные остатку от деления хэша на кол-во бакетов.
    // Хэшом для Integer значений является само число, так как вместимость 2^x > кол-во элеметов, в данном случае 16384 > 10000.
    // Получается, что каждое число помещается в отдельный бакет с ключом равным самому числу.
    // И с помощью .toString() мы итеративно пройдём коллекцию по бакетам от 1 до 10000.
    List<Integer> integers = IntStream.rangeClosed(1, 10000).boxed().collect(Collectors.toList());
    List<Integer> snapshot = new ArrayList<>(integers);
    Collections.shuffle(integers);
    Set<Integer> set = new HashSet<>(integers);
    assert snapshot.toString().equals(set.toString());
  }
}
