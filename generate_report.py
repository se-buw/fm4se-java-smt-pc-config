import os
import re
import xml.etree.ElementTree as ET

TEST_DIR = 'build/test-results/test'

def generate_report_table(headers, data):
  header_row = "| " + " | ".join(headers) + " |\n"
  separator_row = "| " + " | ".join(["---"] * len(headers)) + " |\n"
  f.write(header_row)
  f.write(separator_row)
  for row in data:
    row_bytes = [cell.encode('utf-8').decode('utf-8') if isinstance(cell, str) else str(cell) for cell in row]
    f.write("| " + " | ".join(row_bytes) + " |\n")

def pretty_test_name(test_name):
  test_name = re.sub(r'T(\d+)', r'Task\1:', test_name)
  test_name = re.sub(r'(?<=[a-z])(?=[A-Z])', r' ', test_name)
  test_name = test_name.replace('test', '')
  test_name = test_name.replace('Test', '')
  test_name = test_name.replace('()', '')
  return test_name.strip()


def pretty_message(message):
  message_parts = message.split(':')
  error_type = message_parts[0]
  error_message = message_parts[1]
  if 'AssertionFailedError' in error_type:
    error_message = error_message.split('==>')[0]
  if 'Exception' in error_type:
    error_message = '⚠️ ' + error_type.split('.')[-1]
  return error_message


f = open('report.md', 'w', encoding='utf-8')
f.write('\n# Report\n\n')
headers = ["Test", "Status", "Reason"]

try:
  for filename in os.listdir(TEST_DIR):
    if filename.endswith('.xml'):
      data = []
      title = filename.split('.')[-2]
      f.write('## ' + pretty_test_name(title) + '\n\n')
      root = ET.parse(os.path.join(TEST_DIR, filename))
      for e in root.findall('.//testcase'):
        failure = e.find('failure')
        if failure is not None:
          test_name = pretty_test_name(e.get('name'))
          message = pretty_message(failure.get('message'))
          data.append([test_name, '❌ Failed', message])
        else:
          test_name = pretty_test_name(e.get('name'))
          message = 'Passed'
          data.append([test_name, '✅ Passed', '-'])
          
      generate_report_table(headers, data)
except:
  f.write("\n## Error: No test report found\n\n")
  f.write("### Most likely there are compilation errors\n\n")

f.close()
