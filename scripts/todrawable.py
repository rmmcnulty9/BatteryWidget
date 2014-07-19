import sys

def main():
   
    fname = sys.argv[1]
    f = open(fname, "r")
    lines = f.readlines()
    
    xml = ""
    color = ""
    for line in lines:
        if "----------" in line:
            out = open("res/drawable/color_" + color +".xml", "w")
            out.write(xml)
            xml = ""
            color = ""
            print line
            continue

        if "android:color=" in line:
            color = line.split("""android:color="#""")[1]
            color = color.split("""" />""")[0]
        xml += line


if __name__ == '__main__':
    main()