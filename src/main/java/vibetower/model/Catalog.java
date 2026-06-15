package vibetower.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Catalog {

    public static ArrayList<Item> items() {
        ArrayList<Item> items = new ArrayList<>();

        File folder = new File("png");

        if (!folder.exists() || !folder.isDirectory()) {
            folder = new File("src/main/resources");
        }

        if (!folder.exists() || !folder.isDirectory()) {
            return items;
        }

        File[] files = folder.listFiles();

        if (files == null) {
            return items;
        }

        Arrays.sort(files);

        HashMap<String, Integer> typeCounts = new HashMap<>();

        for (File file : files) {
            String fileName = file.getName();

            if (!fileName.toLowerCase().endsWith(".png")) continue;
            if (fileName.startsWith(".")) continue;
            if (fileName.startsWith("._")) continue;
            if (isServiceImage(fileName)) continue;

            String name = makeName(fileName);
            String category = detectMainCategory(fileName);
            String currency = detectCurrency(fileName);
            int minLevel = detectLevel(fileName);
            String icon = detectIcon(category);

            String typeKey = detectTypeKey(fileName, category);
            int variantIndex = typeCounts.getOrDefault(typeKey, 0);
            typeCounts.put(typeKey, variantIndex + 1);

            int price = detectPrice(fileName, category, currency, variantIndex);

            items.add(new Item(name, category, price, currency, minLevel, icon, fileName));
        }

        return items;
    }

    private static boolean isServiceImage(String fileName) {
        String n = normalize(fileName);

        return n.contains("поле")
                || n.contains("панель")
                || n.contains("фон")
                || n.contains("екран")
                || n.contains("кнопка")
                || n.contains("хрестик")
                || n.contains("назад")
                || n.contains("магазин")
                || n.contains("інвентар")
                || n.contains("ремонт")
                || n.contains("зберегти")
                || n.contains("скасувати")
                || n.contains("змінити")
                || n.contains("монета")
                || n.contains("срібло")
                || n.contains("золото")
                || n.contains("корона")
                || n.contains("serebro")
                || n.contains("zoloto")
                || n.contains("crown")
                || n.contains("zoom")
                || n.contains("edit")
                || n.contains("карта")
                || n.contains("завдання")
                || n.contains("відпочинок")
                || n.contains("apartment")
                || n.contains("komnata")
                || n.contains("room");
    }

    private static String makeName(String fileName) {
        String name = fileName;

        name = name.replace(".png", "");
        name = name.replace("_", " ");
        name = name.replace("-", " ");
        name = name.replace("й", "й");
        name = name.replace("ї", "ї");
        name = name.replace("`", "'");
        name = name.trim();

        if (name.isEmpty()) return "Предмет";

        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static String normalize(String text) {
        if (text == null) return "";

        return text.toLowerCase()
                .replace(".png", "")
                .replace("_", " ")
                .replace("-", " ")
                .replace("й", "й")
                .replace("ї", "ї")
                .replace("`", "'")
                .trim();
    }

    private static String detectMainCategory(String fileName) {
        String n = normalize(fileName);

        if (isVanityTable(n)) return "Столи";
        if (isBathroomItem(n)) return "Санвузол";
        if (isKitchenOnlyItem(n)) return "Кухня";
        if (isTechItem(n)) return "Техніка";
        if (isSofaItem(n)) return "Дивани";
        if (isChairItem(n)) return "Стільці";
        if (isBedItem(n)) return "Ліжка";
        if (isWardrobeItem(n)) return "Шафи";
        if (isTableItem(n)) return "Столи";
        if (isLightItem(n)) return "Освітлення";
        if (isPlantItem(n)) return "Рослини";
        if (isDecorItem(n)) return "Декор";

        return "Декор";
    }

    public static boolean matchesCategory(Item item, String selectedCategory) {
        if (item == null) return false;
        if (selectedCategory == null || selectedCategory.equals("Усе")) return true;

        String name = normalize(item.getName());
        String category = item.getCategory();

        if (selectedCategory.equals(category)) return true;

        if (selectedCategory.equals("Кухня")) {
            return isKitchenOnlyItem(name) || isKitchenTechItem(name);
        }

        if (selectedCategory.equals("Техніка")) {
            return isTechItem(name);
        }

        if (selectedCategory.equals("Санвузол")) {
            return isBathroomItem(name) || name.contains("пральна");
        }

        if (selectedCategory.equals("Декор")) {
            return isDecorItem(name);
        }

        if (selectedCategory.equals("Столи")) {
            return isTableItem(name) || isVanityTable(name);
        }

        return false;
    }

    private static boolean isSofaItem(String n) {
        return n.contains("диван");
    }

    private static boolean isChairItem(String n) {
        return n.contains("стілець")
                || n.contains("кріс")
                || n.contains("пуф");
    }

    private static boolean isBedItem(String n) {
        return n.contains("ліжко");
    }

    private static boolean isWardrobeItem(String n) {
        return n.contains("шафа")
                || n.contains("міні шафа")
                || n.contains("комод")
                || n.contains("стелаж")
                || n.contains("полички");
    }

    private static boolean isTableItem(String n) {
        return n.contains("стіл")
                || n.contains("столик")
                || n.contains("тумба");
    }

    private static boolean isVanityTable(String n) {
        return n.contains("туалетний столик")
                || n.contains("туалетний стіл")
                || n.contains("столик туалетний")
                || n.contains("стіл туалетний");
    }

    private static boolean isLightItem(String n) {
        return n.contains("лампа")
                || n.contains("торшер")
                || n.contains("коршер");
    }

    private static boolean isPlantItem(String n) {
        return n.contains("рослина")
                || n.contains("квіти")
                || n.contains("пальма");
    }

    private static boolean isBathroomItem(String n) {
        if (isVanityTable(n)) return false;

        return n.contains("ванна")
                || n.contains("душ")
                || n.contains("кабіна")
                || n.contains("туалет")
                || n.contains("унітаз")
                || n.contains("раковина")
                || n.contains("вмивальниця")
                || n.contains("джакузі");
    }

    private static boolean isKitchenOnlyItem(String n) {
        return n.contains("кухонні шафи")
                || n.contains("кухонні навісні шафи")
                || n.contains("кухонні нависні шафи")
                || n.contains("кухонні полички")
                || n.contains("полички кухонні")
                || n.contains("стіл кухонний")
                || n.contains("кухонний стіл")
                || n.contains("шафчик кухонний")
                || n.contains("кухонний шафчик");
    }

    private static boolean isKitchenTechItem(String n) {
        return n.contains("холодильник")
                || n.contains("мікрохвильовка")
                || n.contains("плита")
                || n.contains("духовка")
                || n.contains("піч")
                || n.contains("чайник");
    }

    private static boolean isTechItem(String n) {
        return n.contains("холодильник")
                || n.contains("мікрохвильовка")
                || n.contains("плита")
                || n.contains("духовка")
                || n.contains("плита і духовка")
                || n.contains("піч")
                || n.contains("чайник")
                || n.contains("пральна машинка")
                || n.contains("телевізор")
                || n.contains("ноутбук")
                || n.contains("комп'ютер")
                || n.contains("колонка");
    }

    private static boolean isDecorItem(String n) {
        if (isVanityTable(n)) return false;

        return n.contains("килим")
                || n.contains("коврик")
                || n.contains("дзеркало")
                || n.contains("рамка")
                || n.contains("фоторамка")
                || n.contains("двері");
    }

    private static String detectTypeKey(String fileName, String category) {
        String n = normalize(fileName);

        if (isVanityTable(n)) return "туалетний столик";

        if (n.contains("унітаз") || n.contains("туалет")) return "унітаз";
        if (n.contains("раковина") || n.contains("вмивальниця")) return "вмивальниця";
        if (n.contains("ванна")) return "ванна";
        if (n.contains("душ") || n.contains("кабіна")) return "душ";

        if (n.contains("холодильник")) return "холодильник";
        if (n.contains("мікрохвильовка")) return "мікрохвильовка";
        if (n.contains("плита")) return "плита";
        if (n.contains("духовка") || n.contains("піч")) return "духовка";
        if (n.contains("чайник")) return "чайник";
        if (n.contains("пральна")) return "пральна машинка";

        if (n.contains("диван")) return "диван";
        if (n.contains("ліжко")) return "ліжко";
        if (n.contains("міні шафа")) return "міні шафа";
        if (n.contains("шафа")) return "шафа";
        if (n.contains("комод")) return "комод";
        if (n.contains("стелаж")) return "стелаж";

        if (n.contains("стілець")) return "стілець";
        if (n.contains("кріс")) return "крісло";
        if (n.contains("пуф")) return "пуф";

        if (n.contains("столик")) return "столик";
        if (n.contains("стіл")) return "стіл";
        if (n.contains("тумба")) return "тумба";

        if (n.contains("килим") || n.contains("коврик")) return "килим";
        if (n.contains("дзеркало")) return "дзеркало";
        if (n.contains("рамка") || n.contains("фоторамка")) return "рамка";
        if (n.contains("двері")) return "двері";

        if (n.contains("квіти")) return "квіти";
        if (n.contains("пальма")) return "пальма";
        if (n.contains("рослина")) return "рослина";

        if (n.contains("міні лампа")) return "міні лампа";
        if (n.contains("лампа")) return "лампа";
        if (n.contains("торшер") || n.contains("коршер")) return "торшер";

        if (n.contains("кухонні навісні шафи") || n.contains("кухонні нависні шафи")) return "кухонні навісні шафи";
        if (n.contains("кухонні полички")) return "кухонні полички";
        if (n.contains("кухонні шафи")) return "кухонні шафи";
        if (n.contains("кухонний стіл") || n.contains("стіл кухонний")) return "кухонний стіл";

        return category;
    }

    private static String detectCurrency(String fileName) {
        String n = normalize(fileName);

        if (n.contains("диван крутий")
                || n.contains("джакузі")
                || n.contains("телевізор")
                || n.contains("ноутбук")
                || n.contains("комп'ютер")
                || n.contains("колонка велика")) {
            return "gold";
        }

        return "silver";
    }

    private static int detectLevel(String fileName) {
        String n = normalize(fileName);

        if (n.contains("джакузі")
                || n.contains("телевізор")
                || n.contains("ноутбук")
                || n.contains("комп'ютер")
                || n.contains("колонка велика")
                || n.contains("диван крутий")) {
            return 4;
        }

        if (n.contains("холодильник")
                || n.contains("мікрохвильовка")
                || n.contains("плита")
                || n.contains("духовка")
                || n.contains("піч")
                || n.contains("пральна")
                || n.contains("кухонні шафи")
                || n.contains("кухонні навісні шафи")
                || n.contains("кухонні нависні шафи")
                || n.contains("двері")) {
            return 3;
        }

        if (n.contains("ліжко")
                || n.contains("шафа")
                || n.contains("комод")
                || n.contains("стелаж")
                || n.contains("ванна")
                || n.contains("душ")
                || n.contains("туалет")
                || n.contains("унітаз")
                || n.contains("раковина")
                || n.contains("вмивальниця")
                || n.contains("диван")) {
            return 2;
        }

        return 1;
    }

    private static int detectPrice(String fileName, String category, String currency, int variantIndex) {
        String n = normalize(fileName);

        if ("gold".equals(currency)) {
            if (n.contains("диван крутий")) return 3 + variantIndex;
            if (n.contains("телевізор")) return 4 + variantIndex;
            if (n.contains("ноутбук")) return 5 + variantIndex;
            if (n.contains("комп'ютер")) return 6 + variantIndex;
            if (n.contains("колонка велика")) return 4 + variantIndex;
            if (n.contains("джакузі")) return 8 + variantIndex;

            return 3 + variantIndex;
        }

        int basePrice;

        if (n.contains("рамка") || n.contains("фоторамка")) {
            basePrice = 80;
        } else if (n.contains("коврик") || n.contains("килим")) {
            basePrice = 100;
        } else if (n.contains("дзеркало")) {
            basePrice = 180;
        } else if (n.contains("двері")) {
            basePrice = 500;
        } else if (n.contains("квіти")) {
            basePrice = 95;
        } else if (n.contains("рослина")) {
            basePrice = 110;
        } else if (n.contains("пальма")) {
            basePrice = 160;
        } else if (n.contains("міні лампа")) {
            basePrice = 110;
        } else if (n.contains("лампа")) {
            basePrice = 140;
        } else if (n.contains("торшер") || n.contains("коршер")) {
            basePrice = 220;
        } else if (n.contains("стілець")) {
            basePrice = 160;
        } else if (n.contains("пуф")) {
            basePrice = 190;
        } else if (n.contains("кріс")) {
            basePrice = 240;
        } else if (isVanityTable(n)) {
            basePrice = 260;
        } else if (n.contains("столик")) {
            basePrice = 190;
        } else if (n.contains("тумба")) {
            basePrice = 230;
        } else if (n.contains("стіл")) {
            basePrice = 280;
        } else if (n.contains("диван")) {
            basePrice = 420;
        } else if (n.contains("односпальне")) {
            basePrice = 450;
        } else if (n.contains("подвійне")) {
            basePrice = 750;
        } else if (n.contains("ліжко")) {
            basePrice = 580;
        } else if (n.contains("міні шафа")) {
            basePrice = 360;
        } else if (n.contains("комод")) {
            basePrice = 420;
        } else if (n.contains("стелаж")) {
            basePrice = 460;
        } else if (n.contains("шафа")) {
            basePrice = 550;
        } else if (n.contains("туалет") || n.contains("унітаз")) {
            basePrice = 330;
        } else if (n.contains("раковина") || n.contains("вмивальниця")) {
            basePrice = 360;
        } else if (n.contains("ванна")) {
            basePrice = 620;
        } else if (n.contains("душ") || n.contains("кабіна")) {
            basePrice = 700;
        } else if (n.contains("кухонний стіл") || n.contains("стіл кухонний")) {
            basePrice = 320;
        } else if (n.contains("кухонні полички")) {
            basePrice = 340;
        } else if (n.contains("кухонні навісні шафи") || n.contains("кухонні нависні шафи")) {
            basePrice = 480;
        } else if (n.contains("шафчик кухонний") || n.contains("кухонний шафчик")) {
            basePrice = 430;
        } else if (n.contains("кухонні шафи")) {
            basePrice = 580;
        } else if (n.contains("чайник")) {
            basePrice = 180;
        } else if (n.contains("мікрохвильовка")) {
            basePrice = 420;
        } else if (n.contains("плита")) {
            basePrice = 520;
        } else if (n.contains("духовка") || n.contains("піч")) {
            basePrice = 560;
        } else if (n.contains("пральна")) {
            basePrice = 680;
        } else if (n.contains("холодильник")) {
            basePrice = 760;
        } else {
            switch (category) {
                case "Декор": basePrice = 120; break;
                case "Рослини": basePrice = 120; break;
                case "Освітлення": basePrice = 150; break;
                case "Стільці": basePrice = 180; break;
                case "Столи": basePrice = 240; break;
                case "Дивани": basePrice = 420; break;
                case "Ліжка": basePrice = 580; break;
                case "Шафи": basePrice = 460; break;
                case "Санвузол": basePrice = 420; break;
                case "Кухня": basePrice = 440; break;
                case "Техніка": basePrice = 520; break;
                default: basePrice = 150; break;
            }
        }

        return basePrice + getVariantStep(category, variantIndex);
    }

    private static int getVariantStep(String category, int variantIndex) {
        int[] decorSteps = {0, 20, 40, 65, 90, 120};
        int[] smallSteps = {0, 25, 50, 80, 110, 145};
        int[] mediumSteps = {0, 40, 75, 115, 155, 200};
        int[] bigSteps = {0, 60, 110, 170, 230, 300};

        int safeIndex = Math.min(variantIndex, 5);

        switch (category) {
            case "Декор":
            case "Рослини":
                return decorSteps[safeIndex];

            case "Освітлення":
            case "Стільці":
            case "Столи":
                return smallSteps[safeIndex];

            case "Дивани":
            case "Ліжка":
            case "Шафи":
                return mediumSteps[safeIndex];

            case "Санвузол":
            case "Кухня":
            case "Техніка":
                return bigSteps[safeIndex];

            default:
                return smallSteps[safeIndex];
        }
    }

    private static String detectIcon(String category) {
        switch (category) {
            case "Дивани": return "🛋️";
            case "Стільці": return "🪑";
            case "Ліжка": return "🛏️";
            case "Шафи": return "🚪";
            case "Столи": return "🪵";
            case "Техніка": return "⚙️";
            case "Освітлення": return "💡";
            case "Рослини": return "🪴";
            case "Санвузол": return "🛁";
            case "Кухня": return "🍽️";
            default: return "🖼️";
        }
    }

    public static Item findByInventoryName(String text) {
        for (Item item : items()) {
            if (text != null && text.contains(item.getName())) {
                return item;
            }
        }

        return null;
    }
}